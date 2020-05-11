(ns screener.models.sub
  (:require [clojure.spec.alpha :as s]
            [clojure.tools.trace :as trace]
            [screener.models.value-setters :refer :all]
            [screener.models.validations :refer :all]
            [screener.cache.core :as cache]
            [db.operations :as dbops]))

(defrecord Sub
    [adsh
     cik
     name
     sic
     countryba
     stprba
     cityba
     zipba
     bas1
     bas2
     baph
     countryma
     stprma
     cityma
     zipma
     mas1
     mas2
     countryinc
     stprinc
     ein
     former
     changed
     afs
     wksi
     fye
     form
     period
     fy
     fp
     filed
     accepted
     prevrpt
     detail
     instance
     nciks
     aciks])

(defn create-sub
  [sub]
  {:pre [(s/valid? :unq/sub sub)]}
  (let [{:keys [adsh
                cik
                name
                sic
                countryba
                stprba
                cityba
                zipba
                bas1
                bas2
                baph
                countryma
                stprma
                cityma
                zipma
                mas1
                mas2
                countryinc
                stprinc
                ein
                former
                changed
                afs
                wksi
                fye
                form
                period
                fy
                fp
                filed
                accepted
                prevrpt
                detail
                instance
                nciks
                aciks]} sub]
    (->Sub ((string-or-nil) adsh)
           ((string-or-nil) cik)
           ((string-or-nil) name)
           ((string-or-nil) sic)
           ((string-or-nil) countryba)
           ((string-or-nil) stprba)
           ((string-or-nil) cityba)
           ((string-or-nil) zipba)
           ((string-or-nil) bas1)
           ((string-or-nil) bas2)
           ((string-or-nil) baph)
           ((string-or-nil) countryma)
           ((string-or-nil) stprma)
           ((string-or-nil) cityma)
           ((string-or-nil) zipma)
           ((string-or-nil) mas1)
           ((string-or-nil) mas2)
           ((string-or-nil) countryinc)
           ((string-or-nil) stprinc)
           ((string-or-nil) ein)
           ((string-or-nil) former)
           ((date-or-nil) changed "yyyyMMdd")
           ((string-or-nil) afs)
           ((boolean-or-nil) wksi)
           ((string-or-nil) fye)
           ((string-or-nil) form)
           ((date-or-nil) period "yyyyMMdd")
           ((string-or-nil) fy)
           ((string-or-nil) fp)
           ((date-or-nil) filed "yyyyMMdd")
           ((datetime-or-nil) accepted "yyyy-MM-dd HH:mm:ss.S")
           ((boolean-or-nil) prevrpt)
           ((boolean-or-nil) detail)
           ((string-or-nil) instance)
           ((string-or-nil) nciks)
           ((string-or-nil) aciks))))

(def submissions-cache-threshold-value 40)

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
    (dbops/query query-string :sub cik)))

(defn cache-subs
  "Caches the provided list of subs into submissions-cache."
  [subs]
  (if (first subs)
    (do (cache/get-cached-data submissions-cache
                               (create-sub-cache-entry-key (first subs))
                               (fn [key] (first subs)))
        (recur (rest subs)))))

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
        subs (dbops/query query-string :sub cik form)]
    (do (cache-subs-index subs)
        (cache-subs subs)
        subs)))

(defn retrieve-sub
  "Retrieves the associated submission record for the specified cik and adsh from the
   database."
  [cik adsh]
  (let [query-string "SELECT * FROM :table WHERE cik = ? AND adsh = ?"]
    (dbops/query query-string :sub cik adsh)))

