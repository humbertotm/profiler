(ns screener.profiler.core
  (:require [clojure.string :as string]
            [cache.core :as cache]
            [screener.calculations.descriptors :as descriptors]
            [screener.data.tickers :as tickers]
            [screener.data.sub :as sub]
            [screener.data.num :as num]))

;; TODOS:
;; - Time series profiler: return an ordered list of profiling maps for a company during
;;   a range of years.

(defn get-descriptor-function
  "Determines the appropriate symbol for a descriptor function from a descriptor string.
   eg. 'Net Income' => #screener.calculations.core/net-income."
  [descriptor-kw]
  (let [descriptor-fn-name (name descriptor-kw)
        descriptor-fn (resolve (symbol (str
                                        "screener.calculations.descriptors/"
                                        descriptor-fn-name)))]
    (if (nil? descriptor-fn)
      (throw (NullPointerException. (str "Function " descriptor-fn-name " does not exist.")))
      descriptor-fn)))

(defn get-descriptor-key
  "Returns a keyword from a name-string to be employed as key in built profile map.
   eg. 'Net Income' => :NetIncome."
  [name-string]
  (let [split-name (string/split name-string #" ")]
    (keyword (reduce (fn
                       [accum-str next-str]
                       (str accum-str (string/capitalize next-str)))
                     ""
                     split-name))))

(defn descriptor-to-keyword
  "Returns a descriptor string as a keyword to employ as lookup key in descriptor property
   maps.
   'Net Income' => :net-income"
  [descriptor]
  (let [lower-case-name (string/lower-case descriptor)
        split-name (string/split lower-case-name #" ")]
    (keyword (string/join "-" split-name))))

;; Declaring beforehand since it is employed in a mutually recursive definition of calculate.
(declare build-args-map)
(declare calculate)

(defmacro calculate
  "Defines the expression required calculate the provided descriptor for adsh and year.
   Example:
   (calculate :goodwill-to-total-assets 'someadsh' '2019') =>
   (screener.calculations.core/goodwill-to-total-assets {:goodwill 100, :total-assets 1000})"
  [descriptor-kw adsh year]
  `((~get-descriptor-function ~descriptor-kw) (build-args-map ~descriptor-kw ~adsh ~year)))

(defn build-args-map
  "Builds the argument map required for a specific descriptor calculating function as
   defined by screener.calculations.core/descriptor-args-spec map."
  [descriptor-kw adsh year]
  (let [numbers (num/fetch-numbers-for-submission adsh)]
    (reduce (fn [accum next]
              (assoc accum
                     (:name next)
                     (if (= :simple-number (:type next))
                       (:value ((keyword (str ((:name next)
                                               descriptors/simple-number-data-tags)
                                              "|"
                                              year))
                                numbers))
                      (calculate (:name next) adsh year))))
            {}
            (descriptor-kw descriptors/args-spec))))

(defn build-profile-map
  "Builds a profile map from provided list of descriptors for submission corresponding to
   adsh and year, eg.
   {:Descriptor1 2.34
    :Descriptor2 3.14}"
  [descriptors adsh year]
  (reduce (fn
            [accum-map next-descriptor]
            (let [descriptor-keyword (descriptor-to-keyword next-descriptor)]
              (assoc accum-map
                     (get-descriptor-key next-descriptor)
                     (calculate descriptor-keyword adsh year))))
          {}
          descriptors))

(defn build-company-custom-profile
  "Builds a mapping of financial descriptors to values for specified company (ticker)
   and year."
  [descriptors ticker year]
  (let [cik (:cik (tickers/fetch-ticker-cik-mapping ticker))
        adsh (sub/fetch-form-adsh-for-cik-year cik "10-K" year)]
    (if (not (nil? adsh))
      (build-profile-map descriptors adsh year)
      {})))

(defn build-basic-company-profile
  "Same as build-company-custom-profile with a preset list of basic descriptors.
   Mostly used for testing purposes."
  [ticker year]
  (build-company-custom-profile
   '("Current Assets to Current Liabilities"
     "Accounts Payable to Current Assets"
     "Current Assets to Total Liabilities"
     "Total Tangible Assets to Total Liabilities"
     "Goodwill to Total Assets"
     "Net Income"
     "Return on Equity"
     "Return on Working Capital")
   ticker
   year))

(defn profile-list-of-companies
  "Builds a map for a list of companies where keys are tickers and values are a
   profile map containing the specified descriptors for the specified year."
  [tickers-list descriptors year]
  (reduce (fn
            [accum-map ticker]
            (assoc accum-map
                   (keyword ticker)
                   (build-company-custom-profile descriptors ticker year)))
          {}
          tickers-list))

