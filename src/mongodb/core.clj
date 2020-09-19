(ns mongodb.core
  (:import [com.mongodb MongoOptions ServerAddress])
  (:require [environ.core :refer [env]]
            [monger.core :as mg]
            [monger.credentials :as mgcreds]
            [monger.collection :as mgcoll]))

(def host-data
  {:ipaddr (env :mongo-host-ipaddr)
   :port (Integer. (env :mongo-host-port))})

(def user-auth-data
  {:auth-db-name (env :mongo-auth-db)
   :username (env :mongo-username)
   :password (env :mongo-password)})

(def profiler-db-name "profiler")

(def creds
  (mgcreds/create
   (:username user-auth-data)
   (:auth-db-name user-auth-data)
   (.toCharArray (:password user-auth-data))))

(def mongoconn
  (mg/connect-with-credentials
   (:ipaddr host-data)
   (:port host-data)
   creds))

(defn get-profiler-db
  []
  (mg/get-db mongoconn profiler-db-name))

(defn disconnect
  [conn]
  (mg/disconnect conn))

