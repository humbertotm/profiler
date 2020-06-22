(ns screener.profiler.core
  (:require [clojure.string :as string]
            [screener.calculations.core :as calcs]
            [screener.data.tickers :as tickers]
            [screener.data.sub :as sub]
            [screener.data.num :as num]))

(defn build-function-name-from-string
  ""
  [name-string]
  (let [split-name (string/split name-string #" ")]
    (string/lower-case (string/join "-" split-name))))

(defn get-descriptor-function
  ""
  [descriptor]
  (let [descriptor-fn-name (build-function-name-from-string descriptor)
        descriptor-fn (resolve (symbol (str "screener.calculations.core/" descriptor-fn-name)))]
    (if (nil? descriptor-fn)
      (throw (NullPointerException. (str "Function " descriptor-fn-name " does not exist.")))
      descriptor-fn)))

(defn get-descriptor-key
  ""
  [name-string]
  (let [split-name (string/split name-string #" ")]
    (keyword (reduce (fn
                       [accum-str next-str]
                       (str accum-str (string/capitalize next-str)))
                     ""
                     split-name))))

;; For the time being, I'll just let the exception blow up the application.
;; I'll get to handling it later.
(defn build-profile-map
  ""
  [descriptors adsh year]
  (reduce (fn
            [accum-map next-descriptor]
            (assoc accum-map
                   (get-descriptor-key next-descriptor)
                   ((get-descriptor-function next-descriptor) adsh year)))
          {}
          descriptors))

(defn build-company-custom-profile
  ""
  [descriptors ticker year]
  (let [cik (:cik (tickers/get-ticker-cik-mapping ticker))
        adsh (sub/fetch-form-adsh-for-cik-year cik "10-K" year)]
    (do (println (str "cik: " cik ", adsh: " adsh))
        (num/fetch-numbers-for-submission adsh)
        (build-profile-map descriptors adsh year))))

;; (defn build-time-series-profile
;;   ""
;;   [descriptors ticker number-of-years]
;;   ())

