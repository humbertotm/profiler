(ns screener.data.num-test
  (:require [clojure.test :refer :all]
            [screener.data.num :refer :all]
            [helpers.core :refer :all]
            [fixtures.num :refer :all]))

(defn initialize-test-cache
  [f]
  (initialize-numbers-cache)
  (f))

(defn reset-cache
  [f]
  (f)
  (reset-test-cache numbers-cache))

(use-fixtures :once initialize-test-cache)
(use-fixtures :each reset-cache)

(deftest test-create-num-tag-yr-cache-entry-key
  (testing "cache key for sub is :tag|year"
    (is (= (keyword "AccountsReceivableNetCurrent|2019")
           (create-num-tag-yr-cache-entry-key (nth sub-adams-10k-2019-nums 1))))))

(deftest test-map-numbers-to-submission
  (testing "returns a map of numbers keyed with the corresponding adsh"
    (is (= {:NetIncomeLoss|2019 (nth sub-adams-10k-2019-nums 0),
            :AccountsReceivableNetCurrent|2019 (nth sub-adams-10k-2019-nums 1),
            :AccruedLiabilitiesCurrent|2019 (nth sub-adams-10k-2019-nums 2)}
           (map-numbers-to-submission sub-adams-10k-2019-nums)))))

(deftest test-fetch-numbers-for-submission
  (testing "retrieves data from db and caches it"
    (with-redefs [retrieve-numbers-for-submission (fn [adsh] sub-adams-10k-2019-nums)]
      (is (empty? @numbers-cache))
      (is (= 3 (count (fetch-numbers-for-submission "0000002178-19-000087"))))
      (is (= 1 (count @numbers-cache)))
      (is (= 3 (count (get-in @numbers-cache [:0000002178-19-000087]))))))
  (testing "returns cached data"
    (with-redefs [retrieve-numbers-for-submission (fn [adsh] (take 2 sub-adams-10k-2019-nums))]
      (is (= 3 (count (get-in @numbers-cache [:0000002178-19-000087]))))
      (is (= 3 (count (fetch-numbers-for-submission "0000002178-19-000087")))))))

