(ns db.operations
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.string :as str]
            [db.core :as core]))

;; These are simplistic wrappers over jdbc native functions. Will revisit this as progress
;; is made in the project and use cases become more apparent.

(defn query
  "Queries the db with the given parameterized SQL string.
   parameterized-query should include :table in order to be replaced by the table param."
  [parameterized-query table & params]
  (let [sql-query (str/replace parameterized-query #":(\w+)" table)
        full-query (concat [sql-query] params)]
    (jdbc/query (core/connection) full-query)))

(defn insert
  "Inserts the given record into the specified table"
  [table record]
  (jdbc/insert! (core/connection) table record))

(defn execute
  "Executes the provided SQL statement."
  [sql-statement]
  (jdbc/execute! (core/connection) sql-statement))

