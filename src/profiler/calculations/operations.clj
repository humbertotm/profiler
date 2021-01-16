(ns profiler.calculations.operations
  (:require [profiler.calculations.descriptors :as descriptors]))

;; Declaring these two beforehand as there's a mutually recursive relation between them
(declare build-descriptor-args)
(declare calculate)

(defn get-descriptor-computation-fn
  "Determines the appropriate symbol for a descriptor function from a descriptor string.
   eg. 'Net Income' => #profiler.calculations.core/net-income."
  [descriptor-kw]
  (let [computation-fn (:computation-fn (descriptor-kw descriptors/descriptor-spec))]
    (resolve (symbol (str
                      "profiler.calculations.operations/"
                      (name computation-fn))))))

(defmacro calculate
  "Calculates value for provided descriptor-kw using provided numbers. adsh and year are
   employed for looking up the pertinent values in provided numbers."
  [descriptor-kw adsh year numbers]
  `((~get-descriptor-computation-fn ~descriptor-kw)
    (build-descriptor-args ~descriptor-kw ~adsh ~year ~numbers)))

(defn build-descriptor-args
  "Builds the corresponding data structure to be employed to calculate a value. See
   profiler.calculations.descriptors/descriptor-spec for available :computation-fn and
   and the related args data structure."
  [descriptor-kw adsh year numbers]
  (let [descriptor-spec (descriptor-kw descriptors/descriptor-spec)
        computation-fn (:computation-fn descriptor-spec)
        fn-args (:args descriptor-spec)]
    ((resolve (symbol
              (str "profiler.calculations.operations/build-"
                   (name computation-fn)
                   "-args")))
    {:args-spec fn-args, :adsh adsh, :year year, :numbers numbers})))

;; BASIC CALCULATIONS EMPLOYED IN PROFILE DESCRIPTORS
;; AND ITS CORRESPONDING ARGUMENT BUILDING FUNCTION

(defn ratio
  "A simple ratio calculation that does some minimal validation on inputs. Returns a nil
   value if ratio cannot be calculated.
   Formats return value to display two decimal places."
  [{:keys [antecedent consequent]}]
  (if (or (nil? antecedent)
          (or (nil? consequent)
              (zero? consequent)))
    nil
    (/ (double antecedent) (double consequent))))

(defn build-ratio-args
  "Returns a map with the following structure to be employed in calculating a ratio:
   {:antecedent 100.0
    :consequent 200.0}"
  [{:keys [args-spec adsh year numbers]}]
  {:antecedent (calculate
                (:name (:antecedent args-spec))
                adsh
                year
                numbers),
   :consequent (calculate
                (:name (:consequent args-spec))
                adsh
                year
                numbers)})

(defn addition
  "Calculates the resulting value of adding the whole list of sumands provided. Returns nil
   (non computable) if any of the elements in summands-list is nil."
  [summands-list]
  (if (some nil? summands-list)
    nil
    (double (reduce (fn
                      [accum next]
                      (+ accum next))
                    0
                    summands-list))))

(defn build-addition-args
  "Returns a list of values to be employed as summands in addition."
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

(defn simple-number
  "Returns the value provided as a double value. Returns nil for nil values."
  [val]
  (if (nil? val)
    nil
    (double val)))

(defn build-simple-number-args
  "Looks up for the appropriate value in numbers using provided year and extracted tag.
   This is the base case for the mutual recursion between calculate and
   build-descriptor-args."
  [{:keys [args-spec year numbers]}]
  (let [first-choice-keyword (keyword (str (:tag args-spec) "|" year))
        alt-keyword (keyword (str (:alt args-spec) "|" year))
        first-choice-value (:value (first-choice-keyword numbers))
        alt-choice-value (:value (alt-keyword numbers))]
    (if (not (nil? first-choice-value))
      first-choice-value
      alt-choice-value)))

