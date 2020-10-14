(ns screener.profiler.core
  (:import [java.util.concurrent CountDownLatch])
  (:require [clojure.string :as string]
            [screener.calculations.descriptors :as descriptors]
            [screener.calculations.operations :as ops]
            [screener.data.tickers :as tickers]
            [screener.data.sub :as sub]
            [screener.data.num :as num]
            [screener.utils.async :as uasync]
            [mongodb.operations :as mdbops]))

;; TODO: revisit this cache. I don't think it will be needed at all.
;; (def profiles-cache-threshold-value 100)

;; (defn initialize-profiles-cache
;;   "Initializes cache where computed time series profiles for companies will be stored
;;   with the following structure:
;;   {:ticker0 {:2010 {:TangibleAssets 1000000, :ReturnOnEquity 0.09},
;;            :2011 {:TangibleAssets 990000, :ReturnOnEquity 0.08},
;;            :2012 {:TangibleAssets 1200000, :ReturnOnEquity 0.011}},
;;   :ticker1 {:2010 {:TangibleAssets 1000000, :ReturnOnEquity 0.09},
;;            :2011 {:TangibleAssets 990000, :ReturnOnEquity 0.08},
;;            :2012 {:TangibleAssets 1200000, :ReturnOnEquity 0.011}}}"
;;   []
;;   (cache/create-fifo-cache profiles-cache {} profiles-cache-threshold-value))

;; (defn get-descriptor-function
;;   "Determines the appropriate symbol for a descriptor function from a descriptor string.
;;    eg. 'Net Income' => #screener.calculations.core/net-income."
;;   [descriptor-kw]
;;   (let [descriptor-fn-name (name descriptor-kw)
;;         descriptor-fn (resolve (symbol (str
;;                                         "screener.calculations.descriptors/"
;;                                         descriptor-fn-name)))]
;;     (if (nil? descriptor-fn)
;;       (throw (NullPointerException. (str "Function " descriptor-fn-name " does not exist.")))
;;       descriptor-fn)))

;; (defn get-descriptor-calculation-fn
;;   "Determines the appropriate symbol for a descriptor function from a descriptor string.
;;    eg. 'Net Income' => #screener.calculations.core/net-income."
;;   [descriptor-kw]
;;   (let [computation-fn (:computation-fn (descriptor-kw descriptors/descriptor-spec))]
;;     (if (not (nil? computation-fn))
;;       (resolve (symbol (str
;;                         "screener.calculations.operations/"
;;                         (name computation-fn))))
;;       (resolve (symbol "screener.calculations.operations/simple-number")))))

;; (defn get-descriptor-key
;;   "Returns a keyword from a name-string to be employed as key in built profile map.
;;    eg. 'Net Income' => :net_income for eventual JSON doc storage"
;;   [name-string]
;;   (let [split-name (string/split name-string #" ")]
;;     (keyword (reduce (fn
;;                        [accum-str next-str]
;;                        (if (not (empty? accum-str))
;;                          (str accum-str "_" (string/lower-case next-str))
;;                          (string/lower-case next-str)))
;;                      ""
;;                      split-name))))

;; (defn descriptor-to-keyword
;;   "Returns a descriptor string as a keyword to employ as lookup key in descriptor property
;;    maps.
;;    'Net Income' => :net-income"
;;   [descriptor]
;;   (let [lower-case-name (string/lower-case descriptor)
;;         split-name (string/split lower-case-name #" ")]
;;     (keyword (string/join "-" split-name))))

;; Declaring beforehand since it is employed in a mutually recursive definition of calculate.
;; (declare build-args-map)
;; (declare calculate)

;; (declare build-descriptor-args)
;; (declare calculate)

;; (defmacro calculate
;;   "Defines the expression required calculate the provided descriptor for adsh and year.
;;    Example:
;;    (calculate :goodwill-to-total-assets 'someadsh' '2019') =>
;;    (screener.calculations.core/goodwill-to-total-assets {:goodwill 100, :total-assets 1000})"
;;   [descriptor-kw adsh year]
;;   `((~get-descriptor-function ~descriptor-kw) (build-args-map ~descriptor-kw ~adsh ~year)))

;; (defmacro calculate
;;   ""
;;   [descriptor-kw adsh year]
;;   `((~get-descriptor-calculation-fn ~descriptor-kw)
;;     (build-descriptor-args ~descriptor-kw ~adsh ~year)))

;; TODO: extract how cache key for number is constructed to a function for
;; recursion-safe-computation? and build-args-map.
;; (defn recursion-safe-computation?
;;   "Checks that a fallback function is safe to compute by checking that all required args
;;   for computation are present in submission numbers.
;;   Assumes that fallback function arguments are simple numbers. Should this change, this
;;   failsafe mechanism will have to be adjusted."
;;   [fallback-fn-kw adsh year]
;;   (let [fallback-args (fallback-fn-kw descriptors/args-spec)
;;         sub-numbers (num/fetch-numbers-for-submission adsh)
;;         args-key-list (reduce (fn [accum next]
;;                                 (conj accum
;;                                       (keyword (str
;;                                                 (:tag ((:name next)
;;                                                        descriptors/src-number-data-tags))
;;                                                 "|"
;;                                                 year))))
;;                               '()
;;                               fallback-args)]
;;     (reduce (fn [accum next]
;;               (and accum
;;                    (contains? sub-numbers next)))
;;             true
;;             args-key-list)))

;; (defn build-descriptor-args
;;   ""
;;   [descriptor-kw adsh year]
;;   (let [descriptor-spec (descriptor-kw descriptors/descriptor-spec)
;;         computation-fn (:computation-fn descriptor-spec)
;;         fn-args (:args descriptor-spec)
;;         numbers (num/fetch-numbers-for-submission adsh)]
;;     (cond
;;       (= computation-fn :simple-number) (:value ((keyword
;;                                                 (str
;;                                                  (:tag
;;                                                   ((:name fn-args)
;;                                                    descriptors/src-number-data-tags))
;;                                                  "|"
;;                                                  year))
;;                                                numbers))
;;       ;; Return the args map required by the ratio function
;;       (= computation-fn :ratio) {:antecedent (calculate
;;                                               (:name (:antecedent fn-args))
;;                                               adsh
;;                                               year),
;;                                  :consequent (calculate
;;                                               (:name (:consequent fn-args))
;;                                               adsh
;;                                               year)}
;;       (= computation-fn :addition) (reduce (fn
;;                                              [accum next]
;;                                              (let [sign (:sign next)
;;                                                    val (calculate (:name next) adsh year)]
;;                                                (cond
;;                                                  (nil? val) nil
;;                                                  (= :positive sign) (cons val accum)
;;                                                  :else (cons (* -1 val) accum))))
;;                                            '()
;;                                            fn-args)
;;       :else (let [tag (:tag (descriptor-kw descriptors/src-number-data-tags))]
;;               ;; Default case where we're dealing with a plain number that is not to be
;;               ;; included in the profile
;;               (:value ((keyword (str tag "|" year))
;;                        numbers))))))

;; (defn build-args-map
;;   "Builds the argument map required for a specific descriptor calculating function as
;;    defined by screener.calculations.core/descriptor-args-spec map."
;;   [descriptor-kw adsh year]
;;   (let [numbers (num/fetch-numbers-for-submission adsh)]
;;     (reduce (fn [accum next]
;;               (assoc accum
;;                      (:name next)
;;                      (if (= :simple-number (:type next))
;;                        (let [src-value (:value ((keyword
;;                                                  (str
;;                                                   (:tag ((:name next)
;;                                                          descriptors/src-number-data-tags))
;;                                                   "|"
;;                                                   year))
;;                                                 numbers))
;;                              fallback-fn (:fallback ((:name next)
;;                                                      descriptors/src-number-data-tags))]
;;                          (cond
;;                            (not (nil? src-value)) src-value
;;                            (not (nil? fallback-fn)) (if (recursion-safe-computation?
;;                                                          fallback-fn adsh year)
;;                                                       (calculate fallback-fn adsh year)
;;                                                       nil)
;;                            :else nil))
;;                        (calculate (:name next) adsh year))))
;;             {}
;;             (descriptor-kw descriptors/args-spec))))

;; (defn build-profile-map
;;   "Builds a profile map from provided list of descriptors for submission corresponding to
;;    adsh and year, eg.
;;    {:Descriptor1 2.34
;;     :Descriptor2 3.14}"
;;   [descriptors adsh year]
;;   (reduce (fn
;;             [accum-map next-descriptor]
;;             (let [descriptor-keyword (descriptor-to-keyword next-descriptor)]
;;               (assoc accum-map
;;                      (get-descriptor-key next-descriptor)
;;                      (calculate descriptor-keyword adsh year))))
;;           {}
;;           descriptors))

(defn build-profile-map
  ""
  [descriptors adsh year]
  (reduce (fn
            [accum-map next-descriptor]
            (assoc accum-map
                   (keyword (string/replace (name next-descriptor) #"-" "_"))
                   (ops/calculate
                    next-descriptor
                    adsh
                    year
                    (num/fetch-numbers-for-submission adsh))))
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

;; (defn build-basic-company-profile
;;   "Same as build-company-custom-profile with a preset list of basic descriptors.
;;    Mostly used for testing purposes."
;;   [ticker year]
;;   (build-company-custom-profile
;;    '("Current_assets_to Current Liabilities"
;;      "Accounts Payable to Current Assets"
;;      "Current Assets to Total Liabilities"
;;      "Total Tangible Assets to Total Liabilities"
;;      "Goodwill to Total Assets"
;;      "Net Income"
;;      "Return on Equity"
;;      "Return on Working Capital")
;;    ticker
;;    year))

;; TODO: refactor this and the whole descriptors as strings shit.
(defn build-company-full-profile
  ""
  [ticker year]
  (let [cik (:cik (tickers/fetch-ticker-cik-mapping ticker))
        adsh (sub/fetch-form-adsh-for-cik-year cik "10-K" year)]
    (if (not (nil? adsh))
      (build-profile-map
       (keys descriptors/descriptor-spec)
       adsh
       year))))


(defn profile-list-of-companies
  "Builds a map for a list of companies where keys are tickers and values are a
   profile map containing the specified descriptors for the specified year."
  [tickers-list year]
  (reduce (fn
            [accum-map ticker]
            (assoc accum-map
                   (keyword ticker)
                   (build-company-full-profile ticker year)))
          {}
          tickers-list))

(defn company-time-series-custom-profile
  "Builds a time series of profiling maps for requested ticker and descriptor ranging the
  specified years.
  {:2010 {:TangibleAssets 1000000, :ReturnOnEquity 0.09},
   :2011 {:TangibleAssets 990000, :ReturnOnEquity 0.08},
   :2012 {:TangibleAssets 1200000, :ReturnOnEquity 0.011}}"
  [ticker descriptors years]
  (reduce (fn
            [accum-map year]
            (assoc accum-map
                   (keyword year)
                   (build-company-custom-profile descriptors ticker year)))
          {}
          years))

(defn company-time-series-full-profile
  "Builds a time series of profiling maps for requested ticker and descriptor ranging the
  specified years.
  {:2010 {:TangibleAssets 1000000, :ReturnOnEquity 0.09},
   :2011 {:TangibleAssets 990000, :ReturnOnEquity 0.08},
   :2012 {:TangibleAssets 1200000, :ReturnOnEquity 0.011}}"
  [ticker years]
  (reduce (fn
            [accum-map year]
            (assoc accum-map
                   (keyword year)
                   (build-company-full-profile ticker year)))
          {}
          years))

(defn write-yearly-profiles
  ""
  [ticker full-profile]
  (let [kv-list (into (list) full-profile)]
    (uasync/n-threads-exec
     kv-list
     5
     (fn [e] (->> (assoc {:ticker ticker,
                          :year (Integer/parseInt (name (first e)))}
                         :profile
                         (last e))
                  (mdbops/insert-doc "profiles" ,,,))))))

(defn persist-companies-profiles
  "Profiles companies associated to provided list of tickers. Profile is constructed
  according to list of descriptors provided for the range of years specified.
  Output is written to document based database."
  [tickers years]
  (uasync/n-threads-exec
   tickers
   5
   (fn [e] (->> (company-time-series-full-profile e years)
                (write-yearly-profiles e ,,,)))))

