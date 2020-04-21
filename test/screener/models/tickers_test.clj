(ns screener.models.tickers-test
  (:require [clojure.test :refer :all]
            [clojure.core.cache :as cache]
            [screener.models.tickers :refer :all]
            [screener.cache.core :refer [clear-cache]]
            [db.operations :as db-ops]))

;; TODO: Move factories to a fixtures folder in test directory

(defn initialize-cache
  [f]
  (initialize-tickers-cache)
  (f))

(defn load-and-clear-test-data
  [f]
  (db-ops/insert :tickers {:ticker "fb", :cik "1326801"})
  (f)
  (clear-cache cik-tickers-cache)
  (db-ops/execute "TRUNCATE tickers"))

(use-fixtures :once initialize-cache)
(use-fixtures :each load-and-clear-test-data)

(deftest test-create-tickers-cache-entry-key
  (testing "returns a keyword with the form :ticker"
    (is (= (create-tickers-cache-entry-key {:ticker "amzn", :cik "1018724"})
           :amzn))))

(deftest test-retrieve-mapping
  (testing "retrieves mapping from db successfully"
    (is (= {:ticker "fb", :cik "1326801"} (retrieve-mapping "FB")))))

(deftest test-get-ticker-cik-mapping
  (testing "retrieves and caches ticker-cik mapping from db"
    (do (is (empty? (deref screener.models.tickers/cik-tickers-cache)))
        (get-ticker-cik-mapping "FB")
        (is (= {:ticker "fb", :cik "1326801"} (get-in @cik-tickers-cache [:fb]))))))

