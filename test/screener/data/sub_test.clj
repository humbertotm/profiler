(ns screener.data.sub-test
  (:require [clojure.test :refer :all]
            [screener.data.sub :refer :all]
            [helpers.core :refer :all]
            [fixtures.sub :refer :all]))

(defn load-test-subs-data
  [f]
  (load-test-data :submissions
                  (list sub-adams-10q
                        sub-adams-10k-2019
                        sub-adams-10k-2018))
  (f))

(defn clear-test-subs-data
  [f]
  (f)
  (clear-test-table :submissions))

(defn initialize-caches
  [f]
  (initialize-submissions-index-cache)
  (initialize-submissions-cache)
  (f))

(defn reset-caches
  [f]
  (f)
  (reset-test-cache submissions-cache)
  (reset-test-cache submissions-index-cache))

(use-fixtures :once initialize-caches)

(use-fixtures :each
  load-test-subs-data
  clear-test-subs-data
  reset-caches)

;; TODO: test record creation and validations
;; (deftest test-create-sub
;;   (testing "successfully creates Sub record"
;;     ()))

(deftest test-create-sub-cache-entry-key
  (testing "cache key for sub is keyworded adsh"
    (is (= (create-sub-cache-entry-key sub-adams-10q) (keyword (sub-adams-10q :adsh))))))

(deftest test-sub-index-cache-entry-key
  (testing "submissions index cache key has the format :cik|form|fy"
    (is (= (create-sub-index-cache-entry-key sub-adams-10q) :2178|10-Q|2019))))

(deftest test-retrieve-subs-per-cik
  (testing "returns a list of submission records associated to a specific cik"
    (is (= 3 (count (retrieve-subs-per-cik "2178")))))
  (testing "returns empty list when no submissions are associated to cik"
    (is (= 0 (count (retrieve-subs-per-cik "0101"))))))

(deftest test-retrieve-sub
  (testing "returns a sub record for specified cik and adsh"
    (is (= 1 (count (retrieve-sub "2178" "0000002178-19-000086")))))
  (testing "returns an empty list if no submissions records for cik and adsh are found"
    (is (empty? (retrieve-sub "2222" "1234567890-11-222222")))))

(deftest test-cache-subs-index
  (testing "submissions-index-cache is empty before caching subs"
    (is (empty? @submissions-index-cache)))
  (testing "caches list of submissions in submissions-index-cache"
    (do (cache-subs-index (list sub-adams-10k-2019 sub-adams-10k-2018))
        (is (= 2 (count @submissions-index-cache)))
        (is (= "0000002178-19-000087"
               (get-in @submissions-index-cache [:2178|10-K|2019])))
        (is (= "0000002178-19-000080"
               (get-in @submissions-index-cache [:2178|10-K|2018]))))))

(deftest test-retrieve-form-per-cik
  (testing "caches are empty"
    (do (is (empty? @submissions-index-cache))
        (is (empty? @submissions-cache))))
  (testing "pulls records from db and caches them"
    (do (is (empty? @submissions-index-cache))
        (is (= 2 (count (retrieve-form-per-cik "2178" "10-K"))))
        (is (= 2 (count @submissions-index-cache)))
        (is (= 2 (count @submissions-cache))))))

