(ns screener.models.tickers
  (:require [clojure.string :as string]
            [screener.cache.core :as cache]
            [db.operations :as dbops]))

;; GENERAL COMMENT: Make sure that cache is initialized before calling any of the functions
;; in this namespace.

(defn initialize-tickers-cache
  ""
  []
  (cache/create-fifo-cache cik-tickers-cache {} 20))

(defn create-tickers-cache-entry-key
  "Keywords the ticker portion of the mapping to be employed as a cache entry key
   for cik-ticker-mappings-cache."
  [cik-ticker-mapping]
  (keyword (cik-ticker-mapping :ticker)))

(defn retrieve-mapping
  "Data retrieval function to be passed for cache misses"
  [ticker]
  (let [query-string "SELECT * FROM :table WHERE ticker = ?"]
    (dbops/query query-string :ticker (string/lower-case (name ticker)))))

(defn get-ticker-cik-mapping
  "Function employed to retrieve a ticker-cik mapping from the cache if present, database
   otherwise."
  [ticker]
  (cache/get-cached-data cik-tickers-cache
                         (keyword (string/lower-case ticker))
                         screener.models.tickers/retrieve-mapping))

;; TODO: will get back to it later as there is no simple way to handle the
;; WHERE ticker IN ('A', 'B') part of the query.
;; Will have to settle with single record queries for the time being.
;; (defn cache-ticker-list
;;   ""
;;   [tickers]
;;   (let [list-of-tickers (reduce #()
;;                                 '()
;;                                 tickers)
;;         query-string (str "SELECT * FROM :table WHERE ticker IN " list-of-tickers)]
;;     (dbops/query query-string :ticker)))

