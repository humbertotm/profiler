(ns helpers.core
  (:require [db.operations :as db-ops]
            [screener.cache.core :refer [reset-cache]]))

(defn reset-test-cache
  [target-cache]
  (reset-cache target-cache 3))

(defn load-test-data
  [table records]
  (if (first records)
    (do (db-ops/insert table (first records))
        (recur table (rest records)))
    nil))

(defn clear-test-table
  [table]
  (db-ops/execute (str "TRUNCATE " (name table) " CASCADE")))

