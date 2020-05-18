(ns helpers.core
  (:require [db.operations :as db-ops]
            [screener.cache.core :refer [reset-cache]]
            [screener.models.tables :as tables]))

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

;; (defn load-test-data-with-deps
;;   [table records]
;;   (if (not (empty? (data-dependencies table)))
;;     (do (load-test-data (get-test-set (tables/data-dependencies table)))
;;         (load-test-data (get-test-set (tables/data-type-to-table-map table))))
;;     (load-test-data records)))

(defn clear-test-table
  [table]
  (db-ops/execute (str "TRUNCATE " (name table) " CASCADE")))

