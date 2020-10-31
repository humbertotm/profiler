(ns profiler.initialization.core
  (:require [profiler.data.tickers :as tickers]
            [profiler.data.num :as num]
            [profiler.data.sub :as sub]
            [clojure.tools.logging :as log]))

(defn initialize-caches
  "Required caches initialization"
  []
  (log/info "Initializing caches")
  (do (tickers/initialize-tickers-cache)
      (sub/initialize-submissions-index-cache)
      (num/initialize-numbers-cache)))

