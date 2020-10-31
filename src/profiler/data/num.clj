(ns profiler.data.num
  (:require [cache.core :as cache]
            [db.operations :as dbops]
            [profiler.utils.date :refer :all]
            [clojure.tools.logging :as log]))

(def numbers-cache-threshold-value
  "Matches the max db connection pool size"
  5)

(def table-name "numbers")

(defn initialize-numbers-cache
  "Initializes a cache for numbers with the following structure:
   {:adsh0 {:tag0|yr0 {...}, :tag1|yr1 {...}},
    :adsh1 {:tag1|yr1 {...}, :tag2|yr2 {...}}

   The key for each entry is constructed by keywording the related adsh|tag|version.
   TODO: DETERMINE THE APPROPRIATE THRESHOLD VALUE FOR THIS CACHE. DETERMINE IF ANOTHER
         CACHING STRATEGY SUITS THIS USE CASE BETTER THAN FIFO."
  []
  (log/info "Initializing numbers-cache")
  (cache/create-fifo-cache numbers-cache {} numbers-cache-threshold-value))

(defn create-num-tag-yr-cache-entry-key
  "Creates a keyword with the structure :tag|year to be employed as to cached numbers
   associated to a particular adsh. See docstring for initialize-numbers-cache for a
   reference on cache structure."
  [num]
  (let [tag (num :tag)
        year (extract-year (num :ddate))]
    (keyword (str tag "|" year))))

(defn retrieve-numbers-for-submission
  "Retrieves all number records associated to a particular adsh (submission)."
  [adsh]
  (let [query-string "SELECT * FROM :table WHERE adsh = ?"]
    (log/info "Retrieving numbers for submissions with adsh" adsh)
    (dbops/query query-string table-name adsh)))

(defn map-numbers-to-submission
  "Creates a map of number records where keys are of the form :tag|year. The purpose is to
   later associate this collection of records to a particular adsh."
  [numbers]
  (reduce (fn [accum val]
            (assoc accum
                   (create-num-tag-yr-cache-entry-key val)
                   val))
          {}
          numbers))

(defn fetch-numbers-for-submission
  "Fetches all number records for a particular adsh from cache. Retrieves from
   database as fallback."
  [adsh]
  (cache/fetch-cacheable-data numbers-cache
                              (keyword adsh)
                              (fn [key]
                                (->> (retrieve-numbers-for-submission adsh)
                                     (map-numbers-to-submission)))))

