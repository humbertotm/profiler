(ns screener.data.tickers
  (:require [clojure.string :as string]
            [cache.core :as cache]
            [db.operations :as dbops]
            [clojure.tools.logging :as log]))

(def cik-tickers-cache-threshold
  "Matches db pooled data connections max size"
  5)

(def table-name "tickers")

(defn initialize-tickers-cache
  "Initializes a cache with the structure
   {:lowercaseticker0 {:ticker 'ticker0', :cik 'somecik0'},
    :lowercaseticker1 {:ticker 'ticker1', :cik 'somecik1'}}"
  []
  (log/info "Initializing cik-tickers-cache")
  (cache/create-fifo-cache cik-tickers-cache {} cik-tickers-cache-threshold))

(defn reset-cik-tickers-cache
  "Resets cik-tickers-cache to an empty cache."
  []
  (cache/reset-cache cik-tickers-cache cik-tickers-cache-threshold))

(defn create-tickers-cache-entry-key
  "Keywords the ticker portion of the mapping to be employed as a cache entry key
   for cik-ticker-mappings-cache."
  [cik-ticker-mapping]
  (keyword (cik-ticker-mapping :ticker)))

(defn tickers-cache-key
  "Returns ticker as keyword to be employed as a key."
  [ticker]
  (keyword (string/lower-case ticker)))

(defn retrieve-mapping
  "Data retrieval function to be passed for cache misses"
  [ticker]
  (let [query-string "SELECT * FROM :table WHERE ticker = ?"]
    (log/info "Retrieving" ticker "from table" table-name)
    (first (dbops/query query-string table-name (string/lower-case (name ticker))))))

(defn fetch-ticker-cik-mapping
  "Function employed to retrieve a ticker-cik mapping from the cache if present, database
   otherwise."
  [ticker]
  (cache/fetch-cacheable-data cik-tickers-cache
                              (keyword (string/lower-case ticker))
                              retrieve-mapping))

(defn retrieve-ticker-for-cik
  "Retrieve the mapped ticker for provided cik"
  [cik]
  (let [query-string "SELECT * FROM :table WHERE cik = ?"]
    (log/info "Retrieving ticker for cik" cik)
    (:ticker (first (dbops/query query-string table-name cik)))))

