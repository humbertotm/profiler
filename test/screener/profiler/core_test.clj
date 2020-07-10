(ns screener.profiler.core-test
  (:require [clojure.test :refer :all]
            [screener.profiler.core :refer :all]
            [helpers.core :refer :all]
            [fixtures.num :refer :all]))

(defn initialize-caches
  [f]
  (screener.data.sub/initialize-submissions-cache)
  (screener.data.num/initialize-numbers-cache)
  (screener.data.tickers/initialize-tickers-cache)
  (f))

(defn reset-caches
  [f]
  (f)
  (reset-test-cache screener.data.sub/submissions-cache)
  (reset-test-cache screener.data.num/numbers-cache)
  (reset-test-cache screener.data.tickers/cik-tickers-cache))

(use-fixtures :once initialize-caches)
(use-fixtures :each reset-caches)

(deftest test-get-descriptor-function
  (testing "returns a symbol representing a descriptor function"
    (is (= #'screener.calculations.core/net-income
           (get-descriptor-function :net-income))))
  (testing "throws a NullPointerException when it can't be resolved to a function"
    (is (thrown? java.lang.NullPointerException
                 (get-descriptor-function :does-not-exist)))))

(deftest test-get-descriptor-key
  (testing "returns an appropriate keyword for a string"
    (is (= :NetIncome (get-descriptor-key "Net Income"))))
  (testing "does not care about input case"
    (is (= :CurrentAssets (get-descriptor-key "cuRrEnT AsSetS")))))

(deftest test-descriptor-to-keyword
  (testing "returns an appropriate keyword fot a descriptor string"
    (is (= :tangible-assets (descriptor-to-keyword "Tangible Assets"))))
  (testing "does not care about input case"
    (is (= :working-capital (descriptor-to-keyword "workING Capital")))))

(deftest test-build-args-map
  (with-redefs [screener.data.num/retrieve-numbers-for-submission (fn [adsh] build-args-test-numbers)]
    (testing "returns simple arguments map"
      (is (= {:total-assets 45000000.0000M, :goodwill 8450000.0000M}
             (build-args-map :tangible-assets "someadsh" "2019"))))
    (testing "returns recursively constructed arguments map"
      (is (= {:net-income 745000000.0000M, :working-capital 4.0E8}
             (build-args-map :return-on-working-capital "someadsh" "2019"))))
    (testing "returns empty map for not found descriptor"
      (is (= {}
             (build-args-map :caca "someadsh" "2019"))))))

(deftest test-calculate
  (with-redefs [screener.data.num/retrieve-numbers-for-submission (fn [adsh] build-args-test-numbers)]
    (testing "returns expected computed value for simple descriptor"
      (is (= 745000000.0000M
             (calculate :net-income "someadsh" "2019"))))
    (testing "returns expected computed value for complex descriptor"
      (is (= 1.86M
             (calculate :return-on-working-capital "someadsh" "2019"))))
    (testing "throws a NullPointerException when descriptor is not recognized"
      (is (thrown? java.lang.NullPointerException
                   (calculate :does-not-exist "someads" "2019"))))))

(deftest test-build-profile-map
  (with-redefs [screener.data.num/retrieve-numbers-for-submission (fn [adsh] build-args-test-numbers)]
    (testing "returns nicely constructed map with specified descriptors"
      (is (= {:NetIncome 745000000.0000M, :CurrentAssetsToCurrentLiabilities 1.62M}
             (build-profile-map
              '("Net Income", "Current Assets to Current Liabilities")
              "someadsh"
              "2019"))))
    (testing "returns nil values for descriptors that cannot be calculated"
      (is (= {:TangibleAssets 3.655E7, :FreeCashFlow nil}
             (build-profile-map
              '("tangible assets", "free cash flow")
              "someadsh"
              "2019"))))
    (testing "throws NullPointerException when a descriptor is not recognized"
      (is (thrown? java.lang.NullPointerException
                   (build-profile-map '("not exists") "someadsh" "2019"))))))

(deftest test-build-company-custom-profile
  (with-redefs [screener.data.num/retrieve-numbers-for-submission (fn [adsh] build-args-test-numbers)
                screener.data.tickers/retrieve-mapping (fn [t] {:cik "2678", :ticker "adp"})
                screener.data.sub/retrieve-form-from-db (fn [s] "0000234-234234-1")]
    (testing "Returns profile with requested descriptors for company"
      (is (= {:NetIncome 745000000.0000M, :CurrentAssetsToCurrentLiabilities 1.62M}
             (build-company-custom-profile
              '("Net Income", "Current Assets to Current Liabilities")
              "someadsh"
              "2019"))))
    (testing "returns nil values for descriptors that cannot be calculated"
      (is (= {:TangibleAssets 3.655E7, :FreeCashFlow nil}
             (build-company-custom-profile
              '("Tangible Assets", "Free cash flow")
              "someadsh"
              "2019"))))
    (testing "throws a NullPointerException when a descriptor is not recognized"
      (is (thrown? java.lang.NullPointerException
                   (build-company-custom-profile
                    '("Net Income", "Does not exist")
                    "someasdhs"
                    "2019"))))))


