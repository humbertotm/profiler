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

(defn build-company-full-profile
  ""
  [ticker year]
  (let [cik (:cik (tickers/fetch-ticker-cik-mapping ticker))
        adsh (sub/fetch-form-adsh-for-cik-year cik "10-K" year)]
    (if (not (nil? adsh))
      (build-profile-map
       (descriptors/get-available-descriptors)
       adsh
       year))))


;; (defn profile-list-of-companies
;;   "Builds a map for a list of companies where keys are tickers and values are a
;;    profile map containing the specified descriptors for the specified year."
;;   [tickers-list year]
;;   (reduce (fn
;;             [accum-map ticker]
;;             (assoc accum-map
;;                    (keyword ticker)
;;                    (build-company-full-profile ticker year)))
;;           {}
;;           tickers-list))

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

