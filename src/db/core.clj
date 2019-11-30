(ns db.core
  (:require [clojure.java.jdbc :as jdbc])
  (:import (com.mchange.v2.c3p0 ComboPooledDataSource)))

;; TODO: read db implementation details from a config

(def spec
  {
   :classname "org.postgresql.Driver"
   :subprotocol "postgresql"
   :subname "//localhost:5432/screener_dev"
   :user "screeneruser"
   :password "screeneruser"})

(defn pool
  "Returns a pooled datasource from the specified spec"
  [spec]
  (let [cpds (doto (ComboPooledDataSource.)
               (.setDriverClass (:classname spec))
               (.setJdbcUrl (str "jdbc:" (:subprotocol spec) ":" (:subname spec)))
               (.setUser (:user spec))
               (.setPassword (:password spec))
               ;; expire excess connections after 30 minutes of inactivity:
               (.setMaxIdleTimeExcessConnections (* 30 60))
               ;; expire connections after 3 hours of inactivity:
               (.setMaxIdleTime (* 3 60 60)))]
    {:datasource cpds}))

(def pooled-db (delay (pool spec)))

(defn connection
  "Returns a pooled db connection"
  []
  @pooled-db)

