(ns screener.cache.core
  (:require [clojure.core.cache :as cache]))

(defmacro create-fifo-cache
  "Creates a fifo cache with the specified cache-name, seed and threshold value"
  [cache-name seed threshold]
  `(defonce ~cache-name (atom (clojure.core.cache/fifo-cache-factory ~seed :threshold ~threshold))))

;; TODO: define expectations for how key is to be received and how it is to be handled
;; across different record retrievals
;; Who should get the responsibility of transforming the key to its needed form?
;; I'm thinking the user (the retrieve-data function)
(defn get-cached-data
  "Retrieves data from target-cache using the provided key.
   If it is a miss, the provided retrieve-data function will be employed to pull and cache
   the required data.
   Assumes cache has been created with create-fifo-cache and is therefore an atom."
  [target-cache key retrieve-data]
  (cache/lookup (swap! target-cache
                       #(if (cache/has? % key)
                          (cache/hit % key)
                          (cache/miss % key (retrieve-data key))))
                key))

;; TODO: Add test cases for this new function
(defn evict
  "Evicts the provided key from the target-cache"
  [target-cache key]
  (swap! target-cache cache/evict key))

;; (defn clear-cache
;;   "Clears every entry in the target-cache"
;;   [target-cache]
;;   ())

