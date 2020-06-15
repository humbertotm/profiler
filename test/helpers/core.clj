(ns helpers.core
  (:require [db.operations :as db-ops]
            [cache.core :refer [reset-cache]]))

(defmacro get-test-set
  ""
  [model]
  `(~symbol (~str "fixtures." ~model "/test-set")))

(defn reset-test-cache
  [target-cache]
  (reset-cache target-cache 3))

(defn load-test-data
  [table records]
  (if (first records)
    (do (db-ops/insert table (first records))
        (recur table (rest records)))
    nil))

;; TODO: Devise a way to load test data with dependencies.

(defn clear-test-table
  [table]
  (db-ops/execute (str "TRUNCATE " (name table) " CASCADE")))

