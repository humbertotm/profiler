(ns mongodb.operations
  (:require [monger.collection :as mgcoll]
   [mongodb.core :as core]))

(defn insert-doc-and-return
  [target-coll doc]
  (mgcoll/insert-and-return
   (core/get-profiler-db)
   target-coll
   doc))

(defn insert-doc
  [target-coll doc]
  (mgcoll/insert (core/get-profiler-db) target-coll doc))

(defn insert-docs
  [target-coll docs]
  (mgcoll/insert-batch
   (core/get-profiler-db)
   target-coll
   docs))

