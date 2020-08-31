(ns screener.data.sub
  (:require [clojure.string :as string]
            [cache.core :as cache]
            [db.operations :as dbops]))

(def submissions-cache-threshold-value 40)

(def table-name "submissions")

(defn initialize-submissions-cache
  "Initializes a cache for submissions with the following structure:
   {:adsh0 {:adsh 'adsh0', :cik 'cik0, ...},
    :adsh1 {:adsh 'adsh1', :cik 'cik1, ...}}
  
   The key for each entry is the associated keyworded adsh.
   Threshold of 40 elements defined based on the fact that 10K reports will be the most
   employed submissions and we only have available data for 10 years.
   Additionally considering to use at most 4 parallel threads for processing.

   TODO: DEFINE THRESHOLD BASED ON CONFIGURABLE DATA (max thread count, max submission
   count per use case)"
  []
  (cache/create-fifo-cache submissions-cache {} submissions-cache-threshold-value))

(defn initialize-submissions-index-cache
  "Initializes a cache that functions as an index for submissions with following structure:
   {:cik0|form0|year0 'adsh0',
    :cik1|form1|year1 'adsh1'}

   The key for each entry is constructed with the cik, form and fiscal year of the
   associated submission record.
   Only the adsh for the associated submission record is stored to be retrieved and used
   to retrieve the full record from the submissions cache."
  []
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

(defn retrieve-subs-per-cik
  "Returns a list of all the associated submission records for the specified cik."
  [cik]
  (let [query-string "SELECT * FROM :table WHERE cik = ?"]
    (dbops/query query-string table-name cik)))

(defn retrieve-form-from-db
  "Returns adsh string value of submission record described by the provided form-key.
  form-key contains cik, form and year data in the following structure cik|form|year."
  [form-key]
  (let [descriptors (string/split (name form-key) #"\|") ;(cik form year), not lazy
        query-string "SELECT * FROM :table WHERE cik = ? AND form = ? AND fy = ?"
        cik (nth descriptors 0)
        form (nth descriptors 1)
        year (nth descriptors 2)]
    (:adsh (first (dbops/query query-string table-name cik form year)))))

(defn retrieve-form-adsh-from-db
  ""
  [cik form year]
  (let [query-string "SELECT * FROM :table WHERE cik = ? AND form = ? AND fy = ?"]
    (:adsh (first (dbops/query query-string table-name cik form year)))))

(defn fetch-form-adsh-for-cik-year
  "Returns adsh string value for submissions corresponding to specified cik, form and year.
   Looks up in cache as a first resource, falls back to database."
  [cik form year]
  (cache/fetch-cacheable-data submissions-index-cache
                         (keyword (str cik "|" form "|" year))
                         retrieve-form-from-db))

