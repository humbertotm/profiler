(ns screener.calculations.descriptors)

(def descriptor-spec
  ""
  {:current-assets {:computation-fn :simple-number, :args {:tag "AssetsCurrent"}},
   :current-liabilities {:computation-fn :simple-number, :args {:tag "LiabilitiesCurrent"}},
   :accounts-payable {:computation-fn :simple-number, :args {:tag "AccountsPayableCurrent"}},
   :total-liabilities {:computation-fn :simple-number, :args {:tag "Liabilities"}},
   :total-assets {:computation-fn :simple-number, :args {:tag "Assets"}},
   :goodwill {:computation-fn :simple-number, :args {:tag "Goodwill"}},
   :depreciation {:computation-fn :simple-number, :args {:tag "DepreciationDepletionAndAmortization"}},
   :capital-expenditures {:computation-fn :simple-number, :args {:tag "CapitalExpenditures"}},
   :net-income {:computation-fn :simple-number, :args {:tag "NetIncomeLoss"}},
   :total-equity {:computation-fn :simple-number, :args {:tag "StockholdersEquity",} :fallback :calculated-total-equity},
   :common-stock-outstanding {:computation-fn :simple-number, :args {:tag "CommonStockSharesOutstanding"}},
   :stock-options-exercised {:computation-fn :simple-number, :args {:tag "StockIssuedDuringPeriodSharesStockOptionsExercised"}},
   :stock-options-granted {:computation-fn :simple-number, :args {:tag "ShareBasedCompensationArrangementByShareBasedPaymentAwardOptionsGrantsInPeriod"}},
   :stock-repurchase-payment {:computation-fn :simple-number, :args {:tag "PaymentsForRepurchaseOfCommonStock"}},
   :total-sales {:computation-fn :simple-number, :args {:tag "SalesRevenueNet"}},
   :long-term-debt {:computation-fn :simple-number, :args {:tag "LongTermDebt"}},
   :inventory {:computation-fn :simple-number, :args {:tag "InventoryNet"}},
   :dividend-payment {:computation-fn :simple-number, :args {:tag "PaymentsOfDividends"}},
   :dividends-per-share-paid {:computation-fn :simple-number, :args {:tag "CommonStockDividendsPerShareCashPaid"}},
   :operating-income {:computation-fn :simple-number, :args {:tag "OperatingIncomeLoss"}},
   :research-and-development-expense {:computation-fn :simple-number, :args {:tag "ResearchAndDevelopmentExpense"}},
   :tangible-assets {:computation-fn :addition,
                     :args '({:name :total-assets, :sign :positive},
                             {:name :goodwill, :sign :negative})},
   :free-cash-flow {:computation-fn :addition,
                    :args '({:name :net-income, :sign :positive},
                            {:name :depreciation, :sign :positive},
                            {:name :capital-expenditures, :sign :negative})},
   :working-capital {:computation-fn :addition,
                     :args '({:name :current-assets, :sign :positive},
                             {:name :current-liabilities, :sign :negative})},
   :current-assets-to-current-liabilities {:computation-fn :ratio,
                                           :args {:antecedent {:name :current-assets,
                                                               :sign :positive},
                                                  :consequent {:name :current-liabilities,
                                                               :sign :positive}}}
   :accounts-payable-to-current-assets {:computation-fn :ratio,
                                        :args {:antecedent {:name :accounts-payable,
                                                            :sign :positive},
                                               :consequent {:name :current-assets,
                                                            :sign :positive}}}
   :current-assets-to-total-liabilities {:computation-fn :ratio,
                                         :args {:antecedent {:name :current-assets,
                                                             :sign :positive},
                                                :consequent {:name :total-liabilities,
                                                             :sign :positive}}}
   :total-tangible-assets-to-total-liabilities {:computation-fn :ratio,
                                                :args {:antecedent {:name :tangible-assets,
                                                                    :sign :positive},
                                                       :consequent {:name :total-liabilities,
                                                                    :sign :positive}}}
   :goodwill-to-total-assets {:computation-fn :ratio,
                              :args {:antecedent {:name :goodwill,
                                                  :sign :positive},
                                     :consequent {:name :total-assets,
                                                  :sign :positive}}}
   :return-on-equity {:computation-fn :ratio,
                      :args {:antecedent {:name :net-income,
                                          :sign :positive},
                             :consequent {:name :total-equity,
                                          :sign :positive}}}
   :return-on-working-capital {:computation-fn :ratio,
                               :args {:antecedent {:name :net-income,
                                                   :sign :positive},
                                      :consequent {:name :working-capital,
                                                   :sign :positive}}}
   :debt-to-equity {:computation-fn :ratio,
                    :args {:antecedent {:name :total-liabilities,
                                        :sign :positive},
                           :consequent {:name :total-equity,
                                        :sign :positive}}}
   :net-equity {:computation-fn :addition,
                :args '({:name :total-equity, :sign :positive},
                        {:name :goodwill, :sign :negative})}
   :debt-to-net-equity {:computation-fn :ratio,
                        :args {:antecedent {:name :total-liabilities,
                                            :sign :positive},
                               :consequent {:name :net-equity,
                                            :sign :positive}}}
   :comprehensive-stocks-outstanding {:computation-fn :addition,
                                      :args '({:name :common-stock-outstanding, :sign :positive},
                                              {:name :stock-options-granted, :sign :positive})}
   :eps {:computation-fn :ratio,
                 :args {:antecedent {:name :net-income,
                                     :sign :positive},
                        :consequent {:name :common-stock-outstanding,
                                     :sign :positive}}}
   :diluted-eps {:computation-fn :ratio,
                 :args {:antecedent {:name :net-income,
                                     :sign :positive},
                        :consequent {:name :comprehensive-stocks-outstanding,
                                     :sign :positive}}}
   :dividends-paid-to-net-income {:computation-fn :ratio,
                                  :args {:antecedent {:name :dividend-payment,
                                                      :sign :positive},
                                         :consequent {:name :net-income,
                                                      :sign :positive}}}
   :net-profit-margin {:computation-fn :ratio,
                       :args {:antecedent {:name :total-sales,
                                           :sign :positive},
                              :consequent {:name :net-income,
                                           :sign :positive}}}
   :operational-profit-margin {:computation-fn :ratio,
                               :args {:antecedent {:name :operating-income,
                                                   :sign :positive},
                                      :consequent {:name :total-sales,
                                                   :sign :positive}}},
   :research-expense-to-revenue {:computation-fn :ratio,
                                 :args {:antecedent {:name :research-and-development-expense,
                                                     :sign :positive},
                                        :consequent {:name :total-sales,
                                                     :sign :positive}}}})
(defn get-available-descriptors
  ""
  []
  (keys descriptor-spec))

