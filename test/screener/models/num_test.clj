(ns screener.models.num-test
  (:require [clojure.test :refer :all]
            [screener.models.num :refer :all]
            [helpers.core :refer :all]
            [fixtures.num :refer :all]
            [fixtures.sub :as sub-fixtures]
            [fixtures.tag :as tag-fixtures]))

(defn load-test-subs
  [f]
  (load-test-data :submissions sub-fixtures/test-set)
  (f))

(defn load-test-tags
  [f]
  (load-test-data :tags tag-fixtures/test-set)
  (f))

(defn load-test-nums
  [f]
  (load-test-data :numbers sub-adams-10k-2019-nums)
  (f))

(defn clear-subs
  [f]
  (f)
  (clear-test-table :submissions))

(defn clear-tags
  [f]
  (f)
  (clear-test-table :tags))

(defn clear-nums
  [f]
  (f)
  (clear-test-table :numbers))

(defn initialize-test-cache
  [f]
  (initialize-numbers-cache)
  (f))

(defn reset-cache
  [f]
  (f)
  (reset-test-cache numbers-cache))

(use-fixtures :once initialize-test-cache)

;; TODO: implement a rudimentary test data factory that deals with data deps.
(use-fixtures :each
  load-test-subs
  load-test-tags
  load-test-nums
  clear-subs
  clear-tags
  reset-cache)

(deftest test-create-num-cache-entry-key
  (testing "cache key for sub is :adsh|tag|version|year"
    (is (= (keyword "0000002178-19-000087|AccountsReceivableNetCurrent|us-gaap/2019|2019")
         (create-num-cache-entry-key (nth sub-adams-10k-2019-nums 1))))))

(deftest test-fetch-numbers-for-submission
  (testing "retrieves data from db and caches it"
    (do (is (empty? @numbers-cache))
        (fetch-numbers-for-submission "0000002178-19-000087")
        (println @numbers-cache)
        (is (= 1 (count @numbers-cache)))
        (is (= 3 (count (get-in @numbers-cache [:0000002178-19-000087])))))))

