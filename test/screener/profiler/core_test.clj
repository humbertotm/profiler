(ns screener.profiler.core-test
  (:require [clojure.test :refer :all]
            [screener.profiler.core :refer :all]
            [helpers.core :refer :all]
            [fixtures.num :refer :all]))

(defn initialize-caches
  [f]
  (screener.data.sub/initialize-submissions-index-cache)
  (screener.data.num/initialize-numbers-cache)
  (screener.data.tickers/initialize-tickers-cache)
  (f))

(defn reset-caches
  [f]
  (f)
  (reset-test-cache screener.data.sub/submissions-index-cache)
  (reset-test-cache screener.data.num/numbers-cache)
  (reset-test-cache screener.data.tickers/cik-tickers-cache))

(use-fixtures :once initialize-caches)
(use-fixtures :each reset-caches)

(deftest test-get-descriptor-function
  (testing "returns a symbol representing a descriptor function"
    (is (= #'screener.calculations.descriptors/net-income
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
  (with-redefs [screener.data.num/retrieve-numbers-for-submission (fn [adsh] adp-10k-2019-numbers)]
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
  (with-redefs [screener.data.num/retrieve-numbers-for-submission (fn [adsh] adp-10k-2019-numbers)]
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
  (with-redefs [screener.data.num/retrieve-numbers-for-submission (fn [adsh] adp-10k-2019-numbers)]
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
  (with-redefs [screener.data.num/retrieve-numbers-for-submission (fn [_] adp-10k-2019-numbers)
                screener.data.tickers/retrieve-mapping (fn [_] {:cik "2678", :ticker "adp"})
                screener.data.sub/retrieve-form-from-db (fn [_] "0000234-234234-1")]
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

(deftest test-profile-list-of-companies
  (testing "Returns empty map for companies in list not found"
    (with-redefs [screener.data.num/retrieve-numbers-for-submission
                  (let [numbers (atom (list adp-10k-2019-numbers
                                            '()))]
                    (fn [_] (ffirst (swap-vals! numbers rest))))
                  screener.data.tickers/retrieve-mapping
                  (let [mappings (atom (list {:ticker "fb", :cik "8770"} nil))]
                    (fn [_] (ffirst (swap-vals! mappings rest))))
                  screener.data.sub/retrieve-form-from-db
                  (let [subs (atom ["0000002178-19-000087" nil])]
                    (fn [_] (ffirst (swap-vals! subs rest))))]
      (is (= {:fb {:TangibleAssets 3.655E7,
                   :CurrentAssetsToCurrentLiabilities 1.62M},
              :xxx {}}
             (profile-list-of-companies
              '("fb", "xxx")
              '("Tangible Assets", "Current assets to current liabilities")
              "2019")))))
  (testing "Returns expected profiling maps for each company in list"
    (with-redefs [screener.data.num/retrieve-numbers-for-submission
                  (let [numbers (atom (list adp-10k-2019-numbers
                                            avt-10k-2019-numbers))]
                    (fn [_] (ffirst (swap-vals! numbers rest))))
                  screener.data.tickers/retrieve-mapping
                  (let [mappings (atom (list {:ticker "adp", :cik "8670" }
                                             {:ticker "avt", :cik "8858"}))]
                    (fn [_] (ffirst (swap-vals! mappings rest))))
                  screener.data.sub/retrieve-form-from-db
                  (let [subs (atom ["0000002178-19-000089" "0000002222-19-000090"])]
                    (fn [_] (ffirst (swap-vals! subs rest))))]
      (is (= {:adp {:TangibleAssets 3.655E7,
                    :CurrentAssetsToCurrentLiabilities 1.62M},
              :avt {:TangibleAssets 2.51E7,
                    :CurrentAssetsToCurrentLiabilities 1.63M}}
             (profile-list-of-companies
              '("adp", "avt")
              '("Tangible Assets", "Current assets to current liabilities")
              "2019")))))
  (testing "Throws a NullPointerException when a descriptor is not recognized"
    (with-redefs [screener.data.num/retrieve-numbers-for-submission
                  (let [numbers (atom (list adp-10k-2019-numbers
                                            avt-10k-2019-numbers))]
                    (fn [_] (ffirst (swap-vals! numbers rest))))
                  screener.data.tickers/retrieve-mapping
                  (let [mappings (atom (list {:ticker "goog", :cik "8680" }
                                             {:ticker "amzn", :cik "8881"}))]
                    (fn [_] (ffirst (swap-vals! mappings rest))))
                  screener.data.sub/retrieve-form-from-db
                  (let [subs (atom ["0000002178-19-000091" "0000002222-19-000092"])]
                    (fn [_] (ffirst (swap-vals! subs rest))))]
      (is (thrown? java.lang.NullPointerException
                   (profile-list-of-companies
                    '("goog", "amzn")
                    '("Tangible Assets", "Does not exist")
                    "2019"))))))

(deftest test-company-time-series-profile
  (testing "returns profiling map for every year requested"
    (with-redefs [screener.data.num/retrieve-numbers-for-submission
                  (let [numbers (atom adp-numbers)]
                    (fn [_] (last (ffirst (swap-vals! numbers rest)))))
                  screener.data.tickers/retrieve-mapping
                  (fn [_] {:ticker "adp", :cik "8680" }),
                  screener.data.sub/retrieve-form-from-db
                  (let [subs (atom ["0000002178-19-000077"
                                    "0000002178-19-000078"
                                    "0000002178-19-000079"
                                    "0000002178-19-000080"
                                    "0000002178-19-000081"])]
                    (fn [_] (ffirst (swap-vals! subs rest))))]
      (is (= {:2010 {:TangibleAssets 3.655E7, :CurrentAssetsToCurrentLiabilities 1.62M},
              :2011 {:TangibleAssets 3.475E7, :CurrentAssetsToCurrentLiabilities 1.64M},
              :2012 {:TangibleAssets 3.52E7, :CurrentAssetsToCurrentLiabilities 1.63M},
              :2013 {:TangibleAssets 3.565E7, :CurrentAssetsToCurrentLiabilities 1.63M},
              :2014 {:TangibleAssets 3.61E7, :CurrentAssetsToCurrentLiabilities 1.63M}}
             (company-time-series-profile
              "adp"
              '("Tangible Assets", "Current assets to current liabilities")
              '("2010", "2011", "2012", "2013", "2014"))))))
  (testing "returns empty map when company is not found"
    (with-redefs [screener.data.num/retrieve-numbers-for-submission (fn [_] (list))
                  screener.data.tickers/retrieve-mapping (fn [_] nil)
                  screener.data.sub/retrieve-form-from-db (fn [_] nil)]
      (is (= {:2010 {}
              :2011 {}
              :2012 {}
              :2013 {}
              :2014 {}}
             (company-time-series-profile
              "xxx"
              '("Tangible Assets", "Current assets to current liabilities")
              '("2010", "2011", "2012", "2013", "2014"))))))
    (testing "returns nil for descriptors that cannot be calculated"
      (with-redefs [screener.data.num/retrieve-numbers-for-submission
                    (let [numbers (atom adp-numbers)]
                      (fn [_] (last (ffirst (swap-vals! numbers rest)))))
                    screener.data.tickers/retrieve-mapping
                    (fn [_] {:ticker "adp", :cik "8680" }),
                    screener.data.sub/retrieve-form-from-db
                    (let [subs (atom ["0000002178-19-000077"
                                      "0000002178-19-000078"
                                      "0000002178-19-000079"
                                      "0000002178-19-000080"
                                      "0000002178-19-000081"])]
                      (fn [_] (ffirst (swap-vals! subs rest))))]
        (is (= {:2010 {:TangibleAssets 3.655E7, :FreeCashFlow nil},
                :2011 {:TangibleAssets 3.475E7, :FreeCashFlow nil},
                :2012 {:TangibleAssets 3.52E7, :FreeCashFlow nil},
                :2013 {:TangibleAssets 3.565E7, :FreeCashFlow nil},
                :2014 {:TangibleAssets 3.61E7, :FreeCashFlow nil}}
               (company-time-series-profile
                "adp"
                '("Tangible Assets", "Free cash flow")
                '("2010", "2011", "2012", "2013", "2014"))))))
  (testing "throws NullPointerexception when a descriptor is not recognized"
    (with-redefs [screener.data.num/retrieve-numbers-for-submission
                  (let [numbers (atom adp-numbers)]
                    (fn [_] (last (ffirst (swap-vals! numbers rest)))))
                  screener.data.tickers/retrieve-mapping
                  (fn [_] {:ticker "adp", :cik "8680" }),
                  screener.data.sub/retrieve-form-from-db
                  (let [subs (atom ["0000002178-19-000077"
                                    "0000002178-19-000078"
                                    "0000002178-19-000079"
                                    "0000002178-19-000080"
                                    "0000002178-19-000081"])]
                    (fn [_] (ffirst (swap-vals! subs rest))))]
      (is (thrown? java.lang.NullPointerException
                   (company-time-series-profile
                    "adp"
                    '("Tangible Assets", "Does not exist")
                    '("2010", "2011", "2012", "2013", "2014")))))))

;; TODO: test the case where the fallback must be used to calculate some number.

