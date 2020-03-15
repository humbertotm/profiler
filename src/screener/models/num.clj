(ns screener.models.num
  (:require [clojure.spec.alpha :as s]
            [screener.models.value-setters :refer :all]
            [screener.models.validations :refer :all]
            [screener.cache.core :as cache]))

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

(defn create-cache-entry-key
  ""
  [num-map]
  (keyword (str (num-map :adsh) "|" (num-map))))

(defn create-cache-entry-key
  "Creates a keyword with the structure :adsh|tag|version to be employed as the
   cache entry key for numbers-cache."
  [num-map]
  (let [adsh (num-map :adsh)
        tag (num-map :tag)
        version (num-map :version)]
    (keyword (str adsh "|" tag "|" version))))

