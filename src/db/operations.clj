(ns db.operations
  (:require [clojure.java.jdbc :as jdbc])
  (:require [db.core :as core]))

(defn query
  "Queries the db with the given SQL string and parameterized values"
  [sql-string & vals]
  ())

(defn insert
  "Inserts the given record into the specified table"
  [connection table record]
  (jdbc/insert! connection table record))

