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

(defn cache-subs
  "Caches the provided list of subs into submissions-cache."
  [subs]
  (if (first subs)
    (do (cache/get-cached-data submissions-cache
                               (create-sub-cache-entry-key (first subs))
                               (fn [key] (first subs)))
        (recur (rest subs)))))

;; TODO: Need to rethink this whole crap. There are some cases when if there's a cache miss,
;; I want to retrieve from the db. All these functions assume that a list of submissions has
;; been previously retrieved and that I want to cache them if they are not already.
(defn cache-sub
  ""
  [])

(defn cache-subs-index
  "Caches the provided list of subs into the submissions-index-cache."
  [subs]
  (if (first subs)
    (do (cache/get-cached-data submissions-index-cache
                               (create-sub-index-cache-entry-key (first subs))
                               (fn [key] ((first subs) :adsh)))
        (recur (rest subs)))))

(defn retrieve-form-per-cik
  "Retrieves the associated submission records for the specified cik and form (10-K, 10-Q)
   from the database, and caches them in the submissions-index-cache and the
   submissions-cache.
   Returns the list of retrieved submissions."
  [cik form]
  (let [query-string "SELECT * FROM :table WHERE cik = ? AND form = ?"
        subs (dbops/query query-string table-name cik form)]
    (do (cache-subs-index subs)
        (cache-subs subs)
        subs)))

(defn retrieve-form-from-db
  ""
  [form-key]
  (let [descriptors (string/split (name form-key) #"\|") ;(cik form year), not lazy
        query-string "SELECT * FROM :table WHERE cik = ? AND form = ? AND fy = ?"
        cik (nth descriptors 0)
        form (nth descriptors 1)
        year (nth descriptors 2)]
    (:adsh (first (dbops/query query-string table-name cik form year)))))

(defn fetch-form-adsh-for-cik-year
  ""
  [cik form year]
  (cache/get-cached-data submissions-index-cache
                         (keyword (str cik "|" form "|" year))
                         retrieve-form-from-db))

;; TODO: fix this function. It goes to the database every time. Does not check for presence
;; in cache before retrieving.
(defn retrieve-form-per-cik-for-year
  [cik form year]
  (let [query-string "SELECT * FROM :table WHERE cik = ? AND form = ? AND fy = ?"
        sub (dbops/query query-string table-name cik form year)]
    (do (cache-subs-index sub)
        (cache-subs sub)
        sub)))

(defn retrieve-sub
  "Retrieves the associated submission record for the specified cik and adsh from the
   database."
  [cik adsh]
  (let [query-string "SELECT * FROM :table WHERE cik = ? AND adsh = ?"]
    (dbops/query query-string table-name cik adsh)))

