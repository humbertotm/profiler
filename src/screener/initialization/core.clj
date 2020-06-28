(ns screener.initialization.core
  (:require [screener.data.tickers :as tickers]
            [screener.data.num :as num]
            [screener.data.sub :as sub]))

(defn initialize-caches
  "Required caches initialization"
  []
  (do (tickers/initialize-tickers-cache)
      (sub/initialize-submissions-index-cache)
      (sub/initialize-submissions-cache)
      (num/initialize-numbers-cache)))

