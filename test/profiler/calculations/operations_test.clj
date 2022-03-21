(ns profiler.calculations.operations-test
  (:require [clojure.test :refer :all]
            [profiler.calculations.operations :refer :all]
            [fixtures.num :as num-fixtures]))

(deftest test-calculate
  (let [test-numbers
        {:AssetsCurrent|2019 {:value 100.0}
         :LiabilitiesCurrent|2019 {:value 80.0}
         :Goodwill|2019 {:value 20.0}
         :StockholdersEquity|2019 {:value 40.0}
         :NetIncomeLoss|2019 {:value 10.0}}]
    (testing "correctly calculates descriptor with simple (non-recursively computed) args"
      (is (= (double 1.25)
             (calculate :current-assets-to-current-liabilities "adsh" "2019" test-numbers))))
    (testing "correctly calculates descriptor with recursively computed args"
      (is (= (double 0.5)
             (calculate :return-on-working-capital "adsh" "2019" test-numbers))))))

(deftest test-build-descriptor-args
  (let [test-numbers
        {:AssetsCurrent|2019 {:value 100.0}
         :LiabilitiesCurrent|2019 {:value 75.0}
         :Goodwill|2019 {:value 20.0}
         :StockholdersEquity|2019 {:value 40.0}}]
    (testing "returns expected map of args for descriptor computed as ratio"
      (is (= {:antecedent 100.0, :consequent 75.0}
             (build-descriptor-args
              :current-assets-to-current-liabilities
              "someadsh"
              "2019"
              test-numbers))))
    (testing "returns expected list of args for descriptor computed as an addition"
      (is (= '(-20.0 40.0)
             (build-descriptor-args
              :net-equity
              "someadsh"
              "2019"
              test-numbers))))
    (testing "returns expected value for descriptor computed as simple number"
      (is (= 100.0
             (build-descriptor-args
              :current-assets
              "someadsh"
              "2019"
              test-numbers))))
    (testing "throws NullPointerException for unrecognized descriptor"
      (is (thrown? java.lang.NullPointerException
                   (build-descriptor-args
                    :non-existent
                    "someadsh"
                    "2019"
                    test-numbers))))))

(deftest test-get-descriptor-computation-fn
  (testing "returns profiler.calculations.operations/ratio"
    (is (= #'profiler.calculations.operations/ratio
           (get-descriptor-computation-fn :current-assets-to-current-liabilities))))
  (testing "returns profiler.calculations.operations/addition"
    (is (= #'profiler.calculations.operations/addition
           (get-descriptor-computation-fn :free-cash-flow))))
  (testing "returns profiler.calculations.operatioins/simple-number"
    (is (= #'profiler.calculations.operations/simple-number
           (get-descriptor-computation-fn :net-income))))
  (testing "throws a NullPointerException when function is not recognized"
    (is (thrown? java.lang.NullPointerException
                 (get-descriptor-computation-fn :non-existent)))))

(deftest test-ratio
  (testing "returns bigdec with 3 digit precision"
    (is (= (double (/ 1550650 500050))
           (ratio {:antecedent 1550650, :consequent 500050}))))
  (testing "returns nil if divisor is nil"
    (is (nil? (ratio {:antecedent nil, :consequent 1.55}))))
  (testing "returns nil if dividend is nil"
    (is (nil? (ratio {:antecedent 1.55, :consequent nil}))))
  (testing "returns nil if dividend is zero"
    (is (nil? (ratio {:antecedent 1.55, :consequent 0})))))

(deftest test-build-ratio-args
  (let [test-numbers
        {:AssetsCurrent|2019 {:value 100.0}
         :LiabilitiesCurrent|2019 {:value 80.0}
         :Goodwill|2019 {:value 20.0}
         :StockholdersEquity|2019 {:value 40.0}
         :NetIncomeLoss|2019 {:value 10.0}
         :Assets|2019 {:value 130.0}}]
    (testing "returns map of simple values"
      (is (= {:antecedent 100.0,
              :consequent 80.0}
             (build-ratio-args
              {:args-spec {:antecedent {:name :current-assets, :sign :positive},
                           :consequent {:name :current-liabilities, :sign :positive}}
               :adsh "someadsh"
               :year "2019"
               :numbers test-numbers}))))
    (testing "returns map with recursively calculated values"
      (is (= {:antecedent 10.0,
              :consequent 110.0}
             (build-ratio-args
              {:args-spec {:antecedent {:name :net-income, :sign :positive},
                           :consequent {:name :tangible-assets, :sign :positive}}
               :adsh "someadsh"
               :year "2019"
               :numbers test-numbers}))))
    (testing "throws exception when some descriptor is not recognized"
      (is (thrown? java.lang.NullPointerException
                   (build-ratio-args
                    {:args-spec {:antecedent {:name :non-existent, :sign :positive},
                                 :consequent {:name :current-liabilities, :sign :positive}}
                     :adsh "someadsh"
                     :year "2019"
                     :numbers test-numbers}))))))

(deftest test-addition
  (testing "returns sum total of args provided"
    (is (= (double 10)
           (addition '(1 2 3 4)))))
  (testing "returns correct sum total when negative summands are present"
    (is (= (double 10)
           (addition '(1 2 3 -5 4 6 -1)))))
  (testing "returns nil when at least one of the summands is nil"
    (is (nil? (addition '(1 2 nil 3 4 5))))))

(deftest test-build-addition-args
  (let [test-numbers
        {:AssetsCurrent|2019 {:value 100.0}
         :LiabilitiesCurrent|2019 {:value 80.0}
         :Goodwill|2019 {:value 20.0}
         :StockholdersEquity|2019 {:value 40.0}
         :NetIncomeLoss|2019 {:value 10.0}
         :Assets|2019 {:value 130.0}}]
    (testing "returns a list of positive simple values"
      (is (= '(130.0, 100.0)
             (build-addition-args
              {:args-spec '({:name :current-assets, :sign :positive}
                            {:name :total-assets, :sign :positive}),
               :adsh "someadsh",
               :year "2019",
               :numbers test-numbers}))))
    (testing "returns negative values when pertinent"
      (is (= '(130.0 -20.0 100.0)
             (build-addition-args
              {:args-spec '({:name :current-assets, :sign :positive}
                            {:name :goodwill, :sign :negative}
                            {:name :total-assets, :sign :positive}),
               :adsh "someadsh",
               :year "2019",
               :numbers test-numbers}))))
    (testing "returns correct values when a recursive computation is required"
      (is (= '(130.0 -20.0 -110.0)
             (build-addition-args
              {:args-spec '({:name :tangible-assets, :sign :negative}
                            {:name :goodwill, :sign :negative}
                            {:name :total-assets, :sign :positive}),
               :adsh "someadsh",
               :year "2019",
               :numbers test-numbers}))))
    (testing "includes nil in summands list"
      (is (= '(nil -20.0 -110.0)
             (build-addition-args
              {:args-spec '({:name :tangible-assets, :sign :negative}
                            {:name :goodwill, :sign :negative}
                            {:name :total-liabilities, :sign :positive}),
               :adsh "someadsh",
               :year "2019",
               :numbers test-numbers}))))))

(deftest test-simple-number
  (testing "returns value as a double"
    (is (= (double 10)
           (simple-number 10))))
  (testing "returns nil when provided with a nil arg"
    (is (nil? (simple-number nil)))))

(deftest test-build-simple-number-args
  (let [test-numbers
        {:AssetsCurrent|2019 {:value 100.0}
         :LiabilitiesCurrent|2019 {:value 80.0}
         :Goodwill|2019 {:value 20.0}
         :StockholdersEquity|2019 {:value 40.0}
         :NetIncomeLoss|2019 {:value 10.0}
         :Assets|2019 {:value 130.0}
         :CostOfGoodsAndServicesSold|2019 {:value 100.0}}]
    (testing "returns value for present number record"
      (is (= 130.0
             (build-simple-number-args
              {:args-spec {:tag "Assets"}
               :adsh "someadsh"
               :year "2019"
               :numbers test-numbers}))))
    (testing "returns nil for tag not present in numbers"
      (is (nil?
           (build-simple-number-args
            {:args-spec {:tag "NonExistentTag"}
             :adsh "someadsh"
             :year "2019"
             :numbers test-numbers}))))
    (testing "returns value of alternative tag if present"
      (is (= 100.0
             (build-simple-number-args
              {:args-spec {:tag "CostOfGoodsSold" :alt "CostOfGoodsAndServicesSold"}
               :adsh "someadsh"
               :year "2019"
               :numbers test-numbers}))))
    (testing "returns nil if neither first choice or alt are present"
      (is (nil?
           (build-simple-number-args
            {:args-spec {:tag "Crap" :alt "MoreCrap"}
             :adsh "someadsh"
             :year "2019"
             :numbers test-numbers}))))))

