(ns mongodb.core
  (:import [com.mongodb MongoOptions ServerAddress])
  (:require [environ.core :refer [env]]
            [monger.core :as mg]
            [monger.credentials :as mgcreds]
            [monger.collection :as mgcoll]))

(def mongoconn
  "Stores the single mongodb connection employed"
  (atom nil))

(def host-data
  "Reads mongodb host data from environment"
  {:ipaddr (env :mongo-host-ipaddr)
   :port (Integer. (env :mongo-host-port))})

(def user-auth-data
  "Reads mongodb user from environment"
  {:auth-db-name (env :mongo-auth-db)
   :username (env :mongo-username)
   :password (env :mongo-password)})

(def profiler-db-name "profiler")

(def creds
  "Returns a set of creds to be employed in authenticated mongodb connection"
  (mgcreds/create
   (:username user-auth-data)
   (:auth-db-name user-auth-data)
   (.toCharArray (:password user-auth-data))))

(defn initialize-mongoconn
  "Initializes a new mongodb connection"
  []
  (mg/connect-with-credentials
   (:ipaddr host-data)
   (:port host-data)
   creds))

(defn get-mongoconn
  "Returns existing connection. If none, creates and returns a new one"
  []
  (if (nil? @mongoconn)
    (swap! mongoconn (fn [current-conn] (initialize-mongoconn)))
    @mongoconn))

(defn get-profiler-db
  "Returns db to be employed for profiler purposes"
  []
  (mg/get-db (get-mongoconn) profiler-db-name))

(defn disconnect-mongoconn
  "Disconnects existing mongoconn"
  []
  (if (not (nil? @mongoconn))
    (do (mg/disconnect @mongoconn)
        (swap! mongoconn (fn [current-conn] nil)))
    nil))

