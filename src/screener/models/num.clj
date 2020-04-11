(ns screener.models.num
  (:require [clojure.spec.alpha :as s]
            [screener.models.value-setters :refer :all]
            [screener.models.validations :refer :all]
            [screener.cache.core :as cache]
            [db.operations :as dbops]))

(defrecord Num
    [adsh
     tag
     version
     coreg
     ddate
     qtrs
     uom
     value
     footnote])

(defn create-num
  "Creates a new Num record from a map with all-string values from csv"
  [num]
  {:pre [(s/valid? :unq/num num)]}
  (let [{:keys [adsh
                tag
                version
                coreg
                ddate
                qtrs
                uom
                value
                footnote]} num]
    (->Num ((string-or-nil) adsh)
           ((string-or-nil) tag)
           ((string-or-nil) version)
           ((string-or-nil) coreg)
           ((date-or-nil) ddate "yyyyMMdd")
           ((number-or-nil) qtrs)
           ((string-or-nil) uom)
           ((number-or-nil) value)
           ((string-or-nil) footnote))))

(defn initialize-numbers-cache
  "Initializes a cache for numbers with the following structure:
   {:adsh0|tag0|version0 {:adsh 'adsh0', :tag 'tag0', ...},
    :adsh1|tag1|version0 {:adsh 'adsh1', :tag 'tag1', ...}}

   The key for each entry is constructed by keywording the related adsh|tag|version.
   TODO: DETERMINE THE APPROPRIATE THRESHOLD VALUE FOR THIS CACHE. DETERMINE IF ANOTHER
         CACHING STRATEGY SUITS THIS USE CASE BETTER THAN FIFO."
  []
  (cache/create-fifo-cache numbers-cache {} 100))

(defn create-num-cache-entry-key
  "Creates a keyword with the structure :adsh|tag|version to be employed as the
   cache entry key for numbers-cache."
  [num-map]
  (let [adsh (num-map :adsh)
        tag (num-map :tag)
        version (num-map :version)]
    (keyword (str adsh "|" tag "|" version))))

(defn create-adsh-num-cache-entry-key
  ""
  [num-map]
  (let [tag (num-map :tag)
        version (clojure.string/replace (num-map :version) #"/" ".")]
    (keyword (str tag "|" version))))

(defn retrieve-num
  ""
  [adsh tag version]
  (let [query-string "SELECT * FROM :table WHERE adsh = ? AND tag = ? AND version = ?"]
    (dbops/query query-string :num adsh tag version)))

(defn retrieve-nums-per-sub
  ""
  [adsh]
  (let [query-string "SELECT * FROM :table WHERE adsh = ?"]
    (dbops/query query-string :num adsh)))

;; TODO: Define function to store a collection of nums into cache

(defn get-numbers-for-submission
  ""
  [adsh]
  (let [query-string "SELECT * FROM :table WHERE adsh = ?"]
    (dbops/query query-string :num adsh)))

(defn map-numbers-to-submission
  ""
  [numbers]
  (reduce (fn [accum val]
            (assoc accum
                   (create-adsh-num-cache-entry-key val)
                   val))
          {}
          numbers))

(defn cache-numbers
  ""
  [adsh nums-map]
  (cache/get-cached-data numbers-cache
                         (keyword adsh)
                         (fn [key] nums-map)))

(defn cache-numbers-for-submission
  ""
  [adsh]
  (->> (get-numbers-for-submission adsh)
       (map-numbers-to-submission)
       (cache-numbers adsh)))

(defn fetch-numbers-for-submission
  ""
  [adsh]
  (cache/get-cached-data numbers-cache
                         (keyword adsh)
                         (fn [key]
                           (->> (get-numbers-for-submission adsh)
                                (map-numbers-to-submission)))))

