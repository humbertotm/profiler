(ns db.operations
  (:require [clojure.java.jdbc :as jdbc])
  (:require [db.core :as core]))

;; These are simplistic wrappers over jdbc native functions. Will revisit this as progress
;; is made in the project and use cases become more apparent.

(defn query
  "Queries the db with the given SQL string and parameterized values
   Follows the same API as jdbc/query when it comes to the query string and parameterized
   values"
  [sql-query]
  (jdbc/query (core/connection) sql-query))

(defn insert
  "Inserts the given record into the specified table"
  [table record]
  (jdbc/insert! (core/connection) table record))

