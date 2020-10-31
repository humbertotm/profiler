(ns screener.initialization.core
  (:require [screener.data.tickers :as tickers]
            [screener.data.num :as num]
            [screener.data.sub :as sub]
            [clojure.tools.logging :as log]))

(defn initialize-caches
  "Required caches initialization"
  []
  (log/info "Initializing caches")
  (do (tickers/initialize-tickers-cache)
      (sub/initialize-submissions-index-cache)
      (num/initialize-numbers-cache)))

