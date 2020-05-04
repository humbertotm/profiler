(ns screener.models.sub-test
  (:require [clojure.test :refer :all]
            [screener.models.sub :refer :all]
            [helpers.core :refer :all]
            [fixtures.sub :refer :all]))

(defn create-sub-fixture
  [f]
  (def test-sub sub-adams-10q)
  (f))

(defn load-and-clear-test-data
  [f]
  (load-test-data :submissions
                  (list sub-adams-10q
                        sub-adams-10k-2019
                        sub-adams-10k-2018))
  (f)
  (clear-test-table :submissions))

(defn initialize-caches
  [f]
  (initialize-submissions-cache)
  (initialize-submissions-index-cache)
  (f))

(defn clear-caches
  [f]
  (clear-test-cache submissions-index-cache)
  (clear-test-cache submissions-cache)
  (f))

(use-fixtures :once initialize-caches)
(use-fixtures :each
  create-sub-fixture
  load-and-clear-test-data
  clear-caches)

;; TODO
;; (deftest test-create-sub
;;   (testing "successfully creates Sub record"
;;     ()))

(deftest test-create-sub-cache-entry-key
  (testing "cache key for sub is keyworded adsh"
    (is (= (create-sub-cache-entry-key test-sub) (keyword (test-sub :adsh))))))

(deftest test-sub-index-cache-entry-key
  (testing "submissions index cache key has the format :cik|form|fy"
    (is (= (create-sub-index-cache-entry-key test-sub) :2178|10-Q|2019))))

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

;; DEBUG THIS CRAP
(deftest test-retrieve-form-per-cik
  (testing "caches are empty"
    (do (is (empty? (deref submissions-index-cache)))
        (is (empty? (deref submissions-cache)))))
  (testing "pulls records from db when not cached"
    (is (= 2 (retrieve-form-per-cik "2178" "10-K")))))
  (testing "records are cached in submissions index cache"
    (do (is (not (empty? (deref submissions-index-cache))))
        (is (= (((deref submissions-index-cache) :somekey) :someotherkey) "somevalue"))))
  (testing "records are cached in submissions cache"
    (do (is (not (empty? (deref submissions-cache))))
        (is (= 2 (count (deref submissions-cache)))))))

