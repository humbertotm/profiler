(ns screener.models.tickers-test
  (:require [clojure.test :refer :all]
            [screener.models.tickers :refer :all]
            [db.operations :as db-ops]
            [helpers.core :refer :all]
            [fixtures.tickers :refer :all]))

(defn initialize-cache
  [f]
  (initialize-tickers-cache)
  (f))

(defn load-test-tickers-data
  [f]
  (load-test-data :tickers (list test-fb-ticker-mapping))
  (f))

(defn clear-test-tickers-data
  [f]
  (f)
  (clear-test-table :tickers))

(use-fixtures :once initialize-cache)
(use-fixtures :each load-test-tickers-data clear-test-tickers-data)

(deftest test-create-tickers-cache-entry-key
  (testing "returns a keyword with the form :ticker"
    (is (= (create-tickers-cache-entry-key {:ticker "amzn", :cik "1018724"})
           :amzn))))

(deftest test-retrieve-mapping
  (testing "retrieves mapping from db successfully"
    (is (= {:ticker "fb", :cik "1326801"} (retrieve-mapping "FB")))))

(deftest test-get-ticker-cik-mapping
  (testing "retrieves and caches ticker-cik mapping from db"
    (do (is (empty? @cik-tickers-cache))
        (get-ticker-cik-mapping "FB")
        (is (= {:ticker "fb", :cik "1326801"} (get-in @cik-tickers-cache [:fb]))))))

