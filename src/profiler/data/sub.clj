(ns profiler.data.sub
  (:require [clojure.string :as string]
            [cache.core :as cache]
            [db.operations :as dbops]
            [clojure.tools.logging :as log]))

(def submissions-cache-threshold-value
  "Matches pooled data connection max size"
  5)

(def table-name "submissions")

(defn initialize-submissions-index-cache
  "Initializes a cache that functions as an index for submissions with following structure:
   {:cik0|form0|year0 'adsh0',
    :cik1|form1|year1 'adsh1'}

   The key for each entry is constructed with the cik, form and fiscal year of the
   associated submission record.
   Only the adsh for the associated submission record is stored to be retrieved and used
   to retrieve the full record from the submissions cache."
  []
  (log/info "Initializing submissions-index-cache")
  (cache/create-fifo-cache submissions-index-cache {} submissions-cache-threshold-value))

(defn create-sub-cache-entry-key
  "Creates a keyword with the structure :adsh to be employed as the cache entry key for
   submissions-cache."
  [sub-map]
  (let [adsh (sub-map :adsh)]
    (keyword adsh)))

(defn create-sub-index-cache-entry-key
  "Creates a keyword with the structure :cik|form|year to be employed as cache entry key form   the submissions-index-cache."
  [sub-map]
  (let [cik (sub-map :cik)
        form (sub-map :form)
        year (sub-map :fy)]
    (keyword (str cik "|" form "|" year))))

(defn retrieve-form-from-db
  "Returns adsh string value of submission record described by the provided form-key.
  form-key contains cik, form and year data in the following structure cik|form|year."
  [form-key]
  (let [sub-details (string/split (name form-key) #"\|") ;(cik form year), not lazy
        query-string "SELECT * FROM :table WHERE cik = ? AND form = ? AND fy = ?"
        cik (nth sub-details 0)
        form (nth sub-details 1)
        year (nth sub-details 2)]
    (log/info "Retrieving adsh for cik" cik "form" form "year" year)
    (:adsh (first (dbops/query query-string table-name cik form year)))))

(defn fetch-form-adsh-for-cik-year
  "Returns adsh string value for submissions corresponding to specified cik, form and year.
   Looks up in cache as a first resource, falls back to database."
  [cik form year]
  (cache/fetch-cacheable-data submissions-index-cache
                              (keyword (str cik "|" form "|" year))
                              retrieve-form-from-db))

(defn retrieve-10k-full-cik-list
  "Retrieve full list of distinct ciks available for 10-K form submissions"
  []
  (let [query-string "SELECT DISTINCT(cik) FROM :table WHERE form = ?"]
    (dbops/query query-string table-name "10-K")))

