# Financial Ratios and Measures Computation Mechanism

## Available Financial Ratios and Measures
To get a full list of all the ratios and measures that are calculated for each yearly company profile, go to the `descriptors.clj` file in this directory and take a look at the keys in the `descriptor-spec` map.

A neater way to do this would be by calling the `keys` function on the `descriptor-spec` map in a project REPL session:
```Clojure
(keys profiler.calculations.descriptors/descriptor-spec)
```

## Extending the Profile Map
This list is by no means fully comprehensive and as such, it is open for extension.
The calculations mechanism has been designed with this in mind and to include a new measure or financial ratio, it is enough to add it into this map provided the calculation function is already included in the `operations.clj` file in this directory.

Even though there are several descriptors in the output profile map, all of them are computed with one of three functions:
  * **Simple Number**: we retrieve the `value` field from a `num` record.
  * **Ratio**: computed from and `antecedent` and a `consequent`.
  * **Addition**: computed from a n-list of positive or negative summands.
  
#### Adding a New Descriptor

###### Computation Function Already Included

A closer look into the `descriptor-spec` map will reveal a common structure describing how each is to be computed:
```Clojure
{:descriptor0: {:computation-fn :some-computation-fn,
                :args {...}}}
```

The `:computation-fn` value describes which function is to be employed to compute the descriptor. The three options mapping to the ones stated above are:
1. `:simple-number`
2. `:addition`
3. `:ratio`

The `:args` value is a data structure that varies depending on the computation function to be employed. It describes how to construct the argument set to be passed on to the computation function.

**_Simple Number_**

`:args` structure is:
```Clojure
{:tag "AssetsCurrent"}
```

It states what `num` record will be used to extract the needed value by specifying the `:tag` field of the target record. The built arg to be passed on to the `simple-number` function is just a simple `java.lang.Double` value or `nil` if no value is found.

**_Addition_**

`:args` structure is list:
```Clojure
'({:name :total-assets, :sign :positive}, 
  {:name :goodwill, :sign :positive})
```

It states what descriptors are required and what is their sign. The built arg to be passed on to the `addition` function is a list of `java.lang.Double` values or `nil` if a specific value could not be calculated.
```Clojure
'(10.50 nil -5.35)
```

**_Ratio_**

`:args` structure is a map:
```Clojure
{:antecedent {:name :current-assets,
              :sign :positive},
 :consequent {:name :current-liabilities,
              :sign :positive}}
```

Same as for `addition`, each value in the map describes what descriptors are required and what their sign is. The built arg to be passed on to the `ratio` function is a map with the following structure
```Clojure
{:antecedent 2.55,
 :consequent 10.75}
```
where values are either a `java.lang.Double` or `nil` if uncomputable.

In order to add a new descriptor just add its appropriate map defining how it is to be computed and what arguments it requires as described above.

###### Computation Function Not Included

In this case, a couple of new functions will have to be defined in `operations.clj`:
1. The computation function
2. The argument building function for the computation function

Lets say that a `multiplication` function is to be added. The above steps could be done like so:

**Computation function**:
```Clojure
(defn multiplication
  "Calculates the resulting value of multiplying the whole list of multiplicands provided.
  Returns nil (non computable) if any of the elements in multiplicands-list is nil."
  [multiplicands-list]
  (if (some nil? multiplicands-list)
    nil
    (double (reduce (fn
                      [accum next]
                      (* accum next))
                    0
                    multiplicands-list))))
```

**Argument building function**:
```Clojure
(defn build-multiplication-args
  "Returns a list of values to be employed as multiplicands in multiplication."
  [{:keys [args-spec adsh year numbers]}]
  (reduce (fn
            [accum next]
            (let [sign (:sign next)
                  val (calculate (:name next) adsh year numbers)]
              (cond
                (nil? val) (cons nil accum)
                (= :positive sign) (cons (double val) accum)
                :else (cons (double (* -1 val)) accum))))
          '()
          args-spec))
```

In this case, the `build-multiplication-args` function is the same as for addition, but you get the drill.

#### Caveats
  * When adding a new descriptor in `descriptor-spec` map that **is not a** `:simple-number`, make sure that descriptors included in `:args` are included in the map.
  * Avoid circular references between descriptors in `descriptor-spec` as recursive computation is involved.

## How the Calculation Works

In `operations.clj`, the `calculate` macro is the key to understanding how each descriptor is computed. It is mutually recursive with the `build-descriptor-args` function as will be explained next.

The `calculate` macro receives a `descriptor-kw` argument that states which element from the `descriptor-spec` map is being calculated; it corresponds to a key in this map.
It will retrieve the `:computation-fn` and the `:args` data from the value associated to such key and it will proceed first by resolving the symbol for the computation function and second, by building the arguments that will be passed on to this function.

The `build-descriptor-args` function does not make any assumptions on the arguments being simple values than can be easily retrieved from a collection of `num` values. If necessary, it will recursively calculate them. The base case for these mutually recursive functions is the `simple-number` function where a value is retrieved from the provided collection of `num` values.

Lets have a closer look through a simple example, the `:return-on-working-capital` ratio.
This descriptor is declared as follows in the `descriptor-spec` map:
```Clojure
:return-on-working-capital {:computation-fn :ratio,
                            :args {:antecedent {:name :net-income,
                                                :sign :positive},
                            :consequent {:name :working-capital,
                                         :sign :positive}}}
```
It clearly states that it is to be computed as a `ratio`, where the `antecedent` is the `:net-income` descriptor with a `postive` sign and the `consequent` is the `:working-capital` with a `positive` sign.

The fun part comes when building the arguments to be passed to the `ratio` function. Calculating the value for `:net-income` is straightforward as it is a `:simple-number` and the value for it will be retrieved from the collection of numbers provided.
Calculating `:working-capital` will require a recursive calculation. `:working-capital` is not a simple number itself but an `addition` of several other descriptors. Its declaration in `descriptor-spec` is as follows:
```Clojure
:working-capital {:computation-fn :addition,
                  :args '({:name :current-assets, :sign :positive},
                          {:name :current-liabilities, :sign :negative})}
```

`:working-capital` will be calculated as an `addition`. In this case, both involved summands are calculated as `:simple-number`. Once those values have been retrieved, the `addition` for `:working-capital` can be resolved, and finally the `:return-on-working-capital` ratio can be resolved as well.

Having gone through the calculation process, it is clear why no circular declarations for descriptors are allowed. Introducing one will cause the program execution to enter into an infinitely recursive loop.

