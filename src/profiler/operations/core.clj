(ns profiler.operations.core
  (:import [java.util.concurrent CountDownLatch])
  (:require [clojure.string :as string]
            [profiler.calculations.descriptors :as descriptors]
            [profiler.calculations.operations :as ops]
            [profiler.data.tickers :as tickers]
            [profiler.data.sub :as sub]
            [profiler.data.num :as num]
            [profiler.utils.async :as uasync]
            [mongodb.operations :as mdbops]
            [clojure.tools.logging :as log]
            [environ.core :refer [env]]))

(defn build-profile-map
  "Builds profile map for a specific company in a specific year.
  {:descriptor_one 100.0,
   :descriptor_two 100.0}"
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
  "Builds a profile for a specific company employing the full breadth of available
  descriptors"
  [cik year]
  (let [adsh (sub/fetch-form-adsh-for-cik-year cik "10-K" year)]
    (if (not (nil? adsh))
      (build-profile-map
       (descriptors/get-available-descriptors)
       adsh
       year))))

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
  specified years and the full breadth of available descriptors.
  {:2010 {:TangibleAssets 1000000, :ReturnOnEquity 0.09},
   :2011 {:TangibleAssets 990000, :ReturnOnEquity 0.08},
   :2012 {:TangibleAssets 1200000, :ReturnOnEquity 0.011}}"
  [cik years]
  (reduce (fn
            [accum-map year]
            (assoc accum-map
                   (keyword year)
                   (build-company-full-profile cik year)))
          {}
          years))

(defn write-yearly-profiles
  "Function employed to persist computed profile to mongodb"
  [cik ticker full-profile]
  (let [kv-list (into (list) full-profile)]
    (log/info "Persisting profile for cik" cik "ticker" ticker)
    (uasync/n-threads-exec
     kv-list
     (Integer. (env :max-threads))
     (fn [e]
       (->> (assoc {:cik cik,
                    :ticker ticker,
                    :year (Integer/parseInt (name (first e)))}
                   :profile
                   (last e))
            (mdbops/insert-doc "profiles" ,,,))))))

(defn persist-companies-profiles
  "Profiles companies associated to provided list of tickers. Profile is constructed
  according to list of descriptors provided for the range of years specified.
  Output is written to document based database."
  [cik-list years]
  (uasync/n-threads-exec
   cik-list
   (Integer. (env :max-threads))
   (fn [e]
     (let [cik (:cik e)
           ticker (tickers/retrieve-ticker-for-cik cik)]
       (log/info "Computing profile for cik" cik "ticker" ticker)
       (->> (company-time-series-full-profile cik years)
            (write-yearly-profiles cik ticker ,,,))))))

(defn execute-full-profiling
  "Executes full profiling task for full list of available ciks"
  []
  (persist-companies-profiles
   (sub/retrieve-10k-full-cik-list)
   (map #(str %) (range 2009 2020))))

