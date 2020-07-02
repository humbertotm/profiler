(ns cache.core-test
  (:require [clojure.test :refer :all]
            [clojure.core.cache :as cache]
            [cache.core :refer :all]))

(defn create-cache-fixture
  [f]
  (def test-cache (atom (cache/fifo-cache-factory {:a 1, :b 2} :threshold 3)))
  (f)
  (ns-unmap *ns* 'test-cache))

(use-fixtures :each create-cache-fixture)

(defn retrieve-data [key]
  (str key " not found"))

(deftest test-create-fifo-cache
  (testing "cache creation with specified seed"
    (is (instance? clojure.lang.Atom test-cache))))

(deftest test-fetch-cacheable-data
  (testing "successful retrieval of cached data"
    (is (= (fetch-cacheable-data test-cache :a retrieve-data) 1)
        "hit")
    (is (= (fetch-cacheable-data test-cache :c retrieve-data) ":c not found")
        "miss")
    (is (= (do (fetch-cacheable-data test-cache :d retrieve-data)
               (count (deref test-cache)))
           3)
        "max amount of elements in cache is <= threshold")
    (is (= (fetch-cacheable-data test-cache :a retrieve-data) ":a not found")
        "first in evicted after cache exceeds threshold")))

(deftest test-evict-key
  (testing "eviction of specified key from cache"
    (do (is (not (nil? (get-in @test-cache [:b]))))
        (evict-key test-cache :b)
        (is (nil? (get-in @test-cache [:b]))))))

(deftest test-reset-cache
  (testing "clearance of specified cache"
    (do (is (not (empty? @test-cache)))
        (reset-cache test-cache 3)
        (is (instance? clojure.core.cache.FIFOCache @test-cache))
        (= {} @test-cache))))

