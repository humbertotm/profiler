(ns screener.calculations.operations-test
  (:require [clojure.test :refer :all]
            [screener.calculations.operations :refer :all]
            [fixtures.num :as num-fixtures]))

(deftest test-calculate
  ())

(deftest test-build-descriptor-args
  ())

(deftest test-get-descriptor-computation-fn
  ())

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
  (with-redefs [screener.data.num/retrieve-numbers-for-submission
                (fn [adsh] num-fixtures/adp-10k-2019-numbers)]
   (testing "returns map of simple values"
    (is (= {:antecedent 1.045E9,
            :consequent 6.45E8}
           (build-ratio-args
            {:args-spec {:antecedent {:name :current-assets, :sign :positive},
                         :consequent {:name :current-liabilities, :sign :positive}}
             :adsh "someadsh"
             :year "2019"
             :numbers (screener.data.num/fetch-numbers-for-submission "someadsh")}))))
    (testing "returns map with recursively calculated values"
      (is (= {:antecedent 7.45E8,
              :consequent 3.655E7}
             (build-ratio-args
              {:args-spec {:antecedent {:name :net-income, :sign :positive},
                           :consequent {:name :tangible-assets, :sign :positive}}
               :adsh "someadsh"
               :year "2019"
               :numbers (screener.data.num/fetch-numbers-for-submission "someadsh")}))))
  (testing "throws exception when some descriptor is not recognized"
    (is (thrown? java.lang.NullPointerException
           (build-ratio-args
            {:args-spec {:antecedent {:name :non-existent, :sign :positive},
                         :consequent {:name :current-liabilities, :sign :positive}}
             :adsh "someadsh"
             :year "2019"
             :numbers (screener.data.num/fetch-numbers-for-submission "someadsh")}))))))

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
  (with-redefs [screener.data.num/retrieve-numbers-for-submission
                (fn [adsh] num-fixtures/adp-10k-2019-numbers)]
    (testing "returns a list of positive simple values"
      (is (= '(4.5E7, 1.045E9)
             (build-addition-args
              {:args-spec '({:name :current-assets, :sign :positive}
                            {:name :total-assets, :sign :positive}),
               :adsh "someadsh",
               :year "2019",
               :numbers (screener.data.num/fetch-numbers-for-submission "someadsh")}))))
    (testing "returns negative values when pertinent"
      (is (= '(4.5E7 -8450000.0 1.045E9)
             (build-addition-args
              {:args-spec '({:name :current-assets, :sign :positive}
                            {:name :goodwill, :sign :negative}
                            {:name :total-assets, :sign :positive}),
               :adsh "someadsh",
               :year "2019",
               :numbers (screener.data.num/fetch-numbers-for-submission "someadsh")}))))
    (testing "returns correct values when a recursive computation is required"
      (is (= '(4.5E7 -8450000.0 -3.655E7)
             (build-addition-args
              {:args-spec '({:name :tangible-assets, :sign :negative}
                            {:name :goodwill, :sign :negative}
                            {:name :total-assets, :sign :positive}),
               :adsh "someadsh",
               :year "2019",
               :numbers (screener.data.num/fetch-numbers-for-submission "someadsh")}))))))

(deftest test-simple-number
  (testing "returns value as a double"
    (is (= (double 10)
           (simple-number 10))))
  (testing "returns nil when provided with a nil arg"
    (is (nil? (simple-number nil)))))

(deftest test-build-simple-number-args
  (with-redefs [screener.data.num/retrieve-numbers-for-submission
                (fn [adsh] num-fixtures/adp-10k-2019-numbers)]
    (testing "returns value for present number record"
      (is (= 4.5E7
             (build-simple-number-args
              {:args-spec {:tag "Assets"}
               :adsh "someadsh"
               :year "2019"
               :numbers (screener.data.num/fetch-numbers-for-submission "someadsh")}))))
    (testing "returns nil for tag not present in numbers"
      (is (nil?
           (build-simple-number-args
            {:args-spec {:tag "NonExistentTag"}
             :adsh "someadsh"
             :year "2019"
             :numbers (screener.data.num/fetch-numbers-for-submission "someadsh")}))))))

