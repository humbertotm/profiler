(ns db.operations
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.string :as str]
            [db.core :as core]
            [screener.models.core :as models]))

;; These are simplistic wrappers over jdbc native functions. Will revisit this as progress
;; is made in the project and use cases become more apparent.

(defn get-table-name
  "Returns the associated table name for the specified record-type"
  [record-type]
  (name (models/tables record-type)))

(defn query
  "Queries the db with the given parameterized SQL string.
   parameterized-query should include :table in order to be replaced by the table param."
  [parameterized-query record-type & params]
  (let [table (get-table-name record-type)
        sql-query (str/replace parameterized-query #":(\w+)" table)
        full-query (concat [sql-query] params)]
    (jdbc/query (core/connection) full-query)))

(defn insert
  "Inserts the given record into the specified table"
  [table record]
  (jdbc/insert! (core/connection) table record))

