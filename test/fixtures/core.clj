(ns fixtures.core
  (:require [db.operations :as db-ops]
            [screener.cache.core :refer [clear-cache]]))

(defn clear-test-cache
  [target-cache]
  (clear-cache target-cache))

(defn load-test-data
  [table records]
  (if (first records)
    (do (db-ops/insert table (first records))
        (recur table (rest records)))
    nil))

