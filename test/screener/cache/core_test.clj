(ns screener.cache.core-test
  (:require [clojure.test :refer :all]
            [clojure.core.cache :as cache]
            [screener.cache.core :refer :all]))

(defn create-cache-fixture
  [f]
  (create-fifo-cache test-cache {:a 1, :b 2} 3)
  (f)
  (ns-unmap *ns* 'test-cache))

(use-fixtures :each create-cache-fixture)

(defn retrieve-data [key]
  (str key " not found"))

(deftest test-create-fifo-cache
  (testing "cache creation with specified seed"
    (is (instance? clojure.lang.Atom test-cache))))

(deftest test-get-cached-data
  (testing "successful retrieval of cached data"
    (is (= (get-cached-data test-cache :a retrieve-data) 1)
        "hit")
    (is (= (get-cached-data test-cache :c retrieve-data) ":c not found")
        "miss")
    (is (= (do (get-cached-data test-cache :d retrieve-data)
               (count (deref test-cache)))
           3)
        "max amount of elements in cache is <= threshold")
    (is (= (get-cached-data test-cache :a retrieve-data) ":a not found")
        "first in evicted after cache exceeds threshold")))

