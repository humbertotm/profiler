(ns mongodb.operations
  (:require [monger.collection :as mgcoll]
            [mongodb.core :as core]))

(defn insert-doc-and-return
  "Wrapper for mongodb insert-and-return. Fetches pertinent db"
  [target-coll doc]
  (mgcoll/insert-and-return
   (core/get-profiler-db)
   target-coll
   doc))

(defn insert-doc
  "Wrapper for mongodb insert. Fetches pertinent db"
  [target-coll doc]
  (mgcoll/insert (core/get-profiler-db) target-coll doc))

(defn insert-docs
  "Wrapper for mongodb insert-batch. Fetches pertinent db"
  [target-coll docs]
  (mgcoll/insert-batch
   (core/get-profiler-db)
   target-coll
   docs))

(defn clear-coll
  "Removes all documents in target-coll"
  [target-coll]
  (mgcoll/remove (core/get-profiler-db) target-coll))

