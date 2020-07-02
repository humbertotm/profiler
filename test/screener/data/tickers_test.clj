(ns screener.data.tickers-test
  (:require [clojure.test :refer :all]
            [screener.data.tickers :refer :all]
            [helpers.core :refer :all]
            [fixtures.tickers :refer :all]))

(defn initialize-cache
  [f]
  (initialize-tickers-cache)
  (f))

(defn reset-cache
  [f]
  (f)
  (reset-test-cache cik-tickers-cache))

(use-fixtures :once initialize-cache)
(use-fixtures :each reset-cache)

(deftest test-create-tickers-cache-entry-key
  (testing "returns a keyword with the form :ticker"
    (is (= (create-tickers-cache-entry-key {:ticker "amzn", :cik "1018724"})
           :amzn))))

(deftest test-fetch-ticker-cik-mapping
  (testing "retrieves mapping from db and caches it"
    (with-redefs [retrieve-mapping (fn [ticker] {:ticker "amzn", :cik "1018724"})]
      (is (zero? (count @cik-tickers-cache)))
      (is (= {:ticker "amzn", :cik "1018724"}
             (fetch-ticker-cik-mapping "amzn")))
      (is (= 1 (count @cik-tickers-cache)))))
  (testing "returns cached data"
    (is (nil? (get-in @cik-tickers-cache [:fb])))
    (with-redefs [retrieve-mapping (fn [ticker] {:ticker "fb", :cik "1326801"})]
      (is (= {:ticker "fb", :cik "1326801"}
             (fetch-ticker-cik-mapping "fb"))))
    (with-redefs [retrieve-mapping (fn [ticker] {:ticker "fb", :cik "111111"})]
      (is (not (= {:ticker "fb", :cik "111111"}
                  (fetch-ticker-cik-mapping "fb"))))
      (is (= {:ticker "fb", :cik "1326801"}
             (fetch-ticker-cik-mapping "fb"))))))

