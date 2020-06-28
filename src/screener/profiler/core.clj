(ns screener.profiler.core
  (:require [clojure.string :as string]
            [screener.calculations.core :as calcs]
            [screener.data.tickers :as tickers]
            [screener.data.sub :as sub]
            [screener.data.num :as num]))

(defn build-function-name-from-string
  "Constructs the associated string representing the target descriptor calculation function
   from name-string,
   eg. 'Current Assets to Current Liabilites' => current-assets-to-current-liabilities"
  [name-string]
  (let [split-name (string/split name-string #" ")]
    (string/lower-case (string/join "-" split-name))))

(defn get-descriptor-function
  "Determines the appropriate symbol for a descriptor function from a descriptor string.
   eg. 'Net Income' => #screener.calculations.core/net-income."
  [descriptor]
  (let [descriptor-fn-name (build-function-name-from-string descriptor)
        descriptor-fn (resolve (symbol (str "screener.calculations.core/" descriptor-fn-name)))]
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

;; For the time being, I'll just let the exception blow up the application.
;; I'll get to handling exceptions later.
(defn build-profile-map
  "Builds a profile map from provided list of descriptors for submission corresponding to
   adsh and year, eg.
   {:Descriptor1 2.34
    :Descriptor2 3.14}"
  [descriptors adsh year]
  (reduce (fn
            [accum-map next-descriptor]
            (assoc accum-map
                   (get-descriptor-key next-descriptor)
                   ((get-descriptor-function next-descriptor) adsh year)))
          {}
          descriptors))

(defn build-company-custom-profile
  "Returns a company profile map as specified in build-profile-map for ticker, year
   containing the specified descriptor list."
  [descriptors ticker year]
  (let [cik (:cik (tickers/get-ticker-cik-mapping ticker))
        adsh (sub/fetch-form-adsh-for-cik-year cik "10-K" year)]
    (do (num/fetch-numbers-for-submission adsh)
        (build-profile-map descriptors adsh year))))

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

;; (defn build-time-series-profile
;;  pp ""
;;   [descriptors ticker number-of-years]
;;   ())

