(ns screener.models.sub
  (:require [clojure.spec.alpha :as s]
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

(defn initialize-submissions-cache
  "Initializes a cache for submissions with the following structure:
   {:adsh0 {:adsh 'adsh0', :cik 'cik0, ...},
    :adsh1 {:adsh 'adsh1', :cik 'cik1, ...}}
  
   The key for each entry will is the associated keyworded adsh.
   Threshold of 40 elements defined based on the fact that 10K reports will be the most
   employed submissions and we only have available data for 10 years.
   Additionally considering to use at most 4 parallel threads for processing.

   TODO: DEFINE THRESHOLD BASED ON CONFIGURABLE DATA (max thread count, max submission
   count per use case)"
  []
  (cache/create-fifo-cache submissions-cache {} 40))

(defn initialize-submissions-index-cache
  ""
  []
  (cache/create-fifo-cache submissions-index-cache {} 40))

(defn create-sub-cache-entry-key
  "Creates a keyword with the structure :adsh to be employed as the cache entry key for
   submissions-cache."
  [sub-map]
  (let [adsh (sub-map :adsh)]
    (keyword adsh)))

(defn create-sub-index-cache-entry-key
  ""
  [sub-map]
  (let [adsh (sub-map :adsh)
        form (sub-map :form)
        year (sub-map :fy)]
    (keyword (str cik "|" form "|" year))))

(defn retrieve-subs-per-cik
  ""
  [cik]
  (let [query-string "SELECT * FROM :table WHERE cik = ?"]
    (dbops/query query-string :sub cik)))

(defn retrieve-form-per-cik
  ""
  [cik form]
  (let [query-string "SELECT * FROM :table WHERE cik = ? AND form = ?"]
    (cache-subs (dbops/query query-string :sub cik form))))

(defn retrieve-sub
  ""
  [cik adsh]
  (let [query-string "SELECT * FROM :table WHERE cik = ? AND adsh = ?"]
    (dbops/query query-string :sub cik adsh)))

(defn cache-subs
  ""
  [subs]
  (if (first subs)
    (do (cache/get-cached-data submissions-cache
                               (create-sub-cache-entry-key (first subs))
                               (fn [key] (first subs)))
        (recur (rest subs)))
    (println "Done caching retrieved submissions")))

(defn cache-subs-index
  ""
  [subs]
  (if (first subs)
    (do (cache/get-cached-data submissions-index-cache
                               (create-sub-index-cache-entry-key (first subs))
                               (fn [key] (first subs)))
        (recur (rest subs)))
    (println "Done caching retrieved submissions")))

