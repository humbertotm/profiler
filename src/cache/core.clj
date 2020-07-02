(ns cache.core
  (:require [clojure.core.cache :as cache]))

(defmacro create-fifo-cache
  "Creates a fifo cache with the specified cache-name, seed and threshold value"
  [cache-name seed threshold]
  `(defonce ~cache-name (atom (clojure.core.cache/fifo-cache-factory ~seed :threshold ~threshold))))

(defn fetch-cacheable-data
  "Retrieves data from target-cache using the provided key.
   If it is a miss, the provided retrieve-data function will be employed to pull and cache
   the required data.
   Assumes cache has been created with create-fifo-cache and is therefore an atom."
  [target-cache cache-key data-retrieval-fn]
  (cache/lookup (swap! target-cache
                       #(if (cache/has? % cache-key)
                          (cache/hit % cache-key)
                          (cache/miss % cache-key (data-retrieval-fn cache-key))))
                cache-key))

(defn evict-key
  "Evicts the provided key from the target-cache"
  [target-cache key]
  (swap! target-cache cache/evict key))

(defn reset-cache
  "Resets target-cache by resetting its value to an empty FIFOCache.
   Downside: user must specify the threshold value of the reset FIFOCache.
   Used for testing purposes so far."
  [target-cache threshold-value]
  (reset! target-cache
          (clojure.core.cache/fifo-cache-factory {} :threshold threshold-value)))

