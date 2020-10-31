(ns profiler.data.sub-test
  (:require [clojure.test :refer :all]
            [profiler.data.sub :refer :all]
            [helpers.core :refer :all]
            [fixtures.sub :refer :all]))

(defn initialize-cache
  [f]
  (initialize-submissions-index-cache)
  (f))

(defn reset-cache
  [f]
  (f)
  (reset-test-cache submissions-index-cache))

(use-fixtures :once initialize-cache)
(use-fixtures :each reset-cache)

(deftest test-create-sub-cache-entry-key
  (testing "cache key for sub is keyworded adsh"
    (is (= (create-sub-cache-entry-key sub-adams-10q) (keyword (sub-adams-10q :adsh))))))

(deftest test-sub-index-cache-entry-key
  (testing "submissions index cache key has the format :cik|form|fy"
    (is (= (create-sub-index-cache-entry-key sub-adams-10q) :2178|10-Q|2019))))

(deftest test-fetch-form-adsh-for-cik-year
  (testing "retrieves submission from db when not in cache and caches it"
    (with-redefs [retrieve-form-from-db (fn [form-key] sub-adams-10k-2019)]
      (is (zero? (count @submissions-index-cache)))
      (is (= sub-adams-10k-2019
             (fetch-form-adsh-for-cik-year "2178" "10-K" "2019")))
      (is (= 1 (count @submissions-index-cache)))))
  (testing "returns cached value if already present"
    (is (nil? (get-in @submissions-index-cache [:0000002178-19-000086])))
    (with-redefs [retrieve-form-from-db (fn [form-key] sub-adams-10q)]
      (is (= sub-adams-10q
             (fetch-form-adsh-for-cik-year "2178" "10-Q" "2019"))))
    (with-redefs [retrieve-form-from-db (fn [form-key] (assoc sub-adams-10q :adsh "11111"))]
      (is (not (= (assoc sub-adams-10q :adsh "11111")
                  (fetch-form-adsh-for-cik-year "2178" "10-Q" "2019"))))
      (is (= sub-adams-10q
             (fetch-form-adsh-for-cik-year "2178" "10-Q" "2019"))))))

