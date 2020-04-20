(ns db.core
  (:import (com.mchange.v2.c3p0 ComboPooledDataSource))
  (:require [environ.core :refer [env]]))

(def spec
  {:classname "org.postgresql.Driver"
   :subprotocol "postgresql"
   :subname (env :db-subname)
   :user (env :db-user)
   :password (env :db-password)})

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

