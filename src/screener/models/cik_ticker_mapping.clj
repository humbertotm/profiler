(ns screener.models.cik-ticker-mapping
  [clojure.string :as string]
  [screener.cache.core :as cache]
  [db.operations :as dbops])

(defn initialize-cik-ticker-mappings-cache
  ""
  []
  (cache/create-fifo-cache cik-ticker-mappings-cache {} 20))

(defn create-mappings-cache-entry-key
  "Keywords the ticker portion of the mapping to be employed as a cache entry key
   for cik-ticker-mappings-cache."
  [cik-ticker-mapping]
  (keyword (cik-ticker-mapping :ticker)))

(def retrieve-mapping
  "Data retrieval function to be passed for cache misses"
  [ticker]
  (let [lc-ticker (string/lower-case ticker)
        query-string "SELECT * FROM :table WHERE ticker = ?"]
    (dbops/query query-string :cik-ticker lc-ticker)))

