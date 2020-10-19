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

(deftest test-build-profile-map
  (with-redefs [screener.data.num/retrieve-numbers-for-submission (fn [adsh] adp-10k-2019-numbers)]
    (testing "returns nicely constructed map with specified descriptors"
      (is (= {:net_income 7.45E8, :current_assets_to_current_liabilities 1.62015503875969}
             (build-profile-map
              '(:net-income, :current-assets-to-current-liabilities)
              "someadsh"
              "2019"))))
    (testing "returns nil values for descriptors that cannot be calculated"
      (is (= {:tangible_assets 3.655E7, :free_cash_flow nil}
             (build-profile-map
              '(:tangible-assets, :free-cash-flow)
              "someadsh"
              "2019"))))
    (testing "throws NullPointerException when a descriptor is not recognized"
      (is (thrown? java.lang.NullPointerException
                   (build-profile-map '(:not-exists) "someadsh" "2019"))))))

(deftest test-build-company-custom-profile
  (with-redefs [screener.data.num/retrieve-numbers-for-submission (fn [_] adp-10k-2019-numbers)
                screener.data.tickers/retrieve-mapping (fn [_] {:cik "2678", :ticker "adp"})
                screener.data.sub/retrieve-form-from-db (fn [_] "0000234-234234-1")]
    (testing "Returns profile with requested descriptors for company"
      (is (= {:net_income 7.45E8, :current_assets_to_current_liabilities 1.62015503875969}
             (build-company-custom-profile
              '(:net-income, :current-assets-to-current-liabilities)
              "someadsh"
              "2019"))))
    (testing "returns nil values for descriptors that cannot be calculated"
      (is (= {:tangible_assets 3.655E7, :free_cash_flow nil}
             (build-company-custom-profile
              '(:tangible-assets, :free-cash-flow)
              "someadsh"
              "2019"))))
    (testing "throws a NullPointerException when a descriptor is not recognized"
      (is (thrown? java.lang.NullPointerException
                   (build-company-custom-profile
                    '(:net-income, :does-not-exist)
                    "someasdhs"
                    "2019"))))))

(deftest test-build-company-full-profile
  (with-redefs [screener.data.num/retrieve-numbers-for-submission (fn [_] adp-10k-2019-numbers)
                screener.data.tickers/retrieve-mapping (fn [_] {:cik "2678", :ticker "adp"})
                screener.data.sub/retrieve-form-from-db (fn [_] "0000234-234234-1")]
    (testing "Returns profile with requested descriptors for company"
      (is (= {:current_assets_to_current_liabilities 1.62015503875969,	  
	      :accounts_payable nil,
	      :total_equity nil,
	      :research_expense_to_revenue nil,
	      :common_stock_outstanding nil,
	      :debt_to_equity nil,
	      :current_assets 1.045E9,
	      :total_assets 4.5E7,
	      :dividend_payment nil,
	      :total_tangible_assets_to_total_liabilities nil,
	      :stock_repurchase_payment nil,
	      :working_capital 4.0E8,
	      :accounts_payable_to_current_assets nil,
	      :depreciation nil,
	      :total_liabilities nil,
	      :current_liabilities 6.45E8,
	      :debt_to_net_equity nil,
	      :goodwill 8450000.0,
	      :current_assets_to_total_liabilities nil,
	      :inventory nil,
	      :operational_profit_margin nil,
	      :net_income 7.45E8,
	      :return_on_working_capital 1.8625,
	      :operating_income nil,
	      :research_and_development_expense nil,
	      :stock_options_exercised nil,
	      :stock_options_granted nil,
	      :tangible_assets 3.655E7,
	      :dividends_paid_to_net_income nil,
	      :net_equity nil,
	      :total_sales nil,
	      :dividends_per_share_paid nil,
	      :return_on_equity nil,
	      :goodwill_to_total_assets 0.18777777777777777,
	      :long_term_debt nil,
	      :net_profit_margin nil,
	      :diluted_eps nil,
	      :comprehensive_stocks_outstanding nil,
	      :free_cash_flow nil,
	      :capital_expenditures nil,
	      :eps nil}
             (build-company-full-profile
              "someadsh"
              "2019"))))))

(deftest test-company-time-series-custom-profile
  (testing "returns profiling map for every year requested"
    (with-redefs [screener.data.num/retrieve-numbers-for-submission
                  (let [numbers (atom adp-numbers)]
                    (fn [_] (last (ffirst (swap-vals! numbers rest)))))
                  screener.data.tickers/retrieve-mapping
                  (fn [_] {:ticker "adp", :cik "8680" })
                  screener.data.sub/retrieve-form-from-db
                  (let [subs (atom ["0000002178-19-000077"
                                    "0000002178-19-000078"
                                    "0000002178-19-000079"
                                    "0000002178-19-000080"
                                    "0000002178-19-000081"])]
                    (fn [_] (ffirst (swap-vals! subs rest))))]
      (is (= {:2010 {:tangible_assets 3.655E7, :current_assets_to_current_liabilities 1.62015503875969},
              :2011 {:tangible_assets 3.475E7, :current_assets_to_current_liabilities 1.64},
              :2012 {:tangible_assets 3.52E7, :current_assets_to_current_liabilities 1.6349206349206349},
              :2013 {:tangible_assets 3.565E7, :current_assets_to_current_liabilities 1.6299212598425197},
              :2014 {:tangible_assets 3.61E7, :current_assets_to_current_liabilities 1.625}}
             (company-time-series-custom-profile
              "adp"
              '(:tangible-assets, :current-assets-to-current-liabilities)
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
             (company-time-series-custom-profile
              "xxx"
              '(:tangible-assets, :current-assets-to-current-liabilities)
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
        (is (= {:2010 {:tangible_assets 3.655E7, :free_cash_flow nil},
                :2011 {:tangible_assets 3.475E7, :free_cash_flow nil},
                :2012 {:tangible_assets 3.52E7, :free_cash_flow nil},
                :2013 {:tangible_assets 3.565E7, :free_cash_flow nil},
                :2014 {:tangible_assets 3.61E7, :free_cash_flow nil}}
               (company-time-series-custom-profile
                "adp"
                '(:tangible-assets, :free-cash-flow)
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
                   (company-time-series-custom-profile
                    "adp"
                    '(:tangible-assets, :does-not-exist)
                    '("2010", "2011", "2012", "2013", "2014")))))))

(deftest test-company-time-series-full-profile
  ())

