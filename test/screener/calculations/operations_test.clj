(ns screener.calculations.operations-test
  (:require [clojure.test :refer :all]
            [screener.calculations.operations :refer :all]))

(deftest test-ratio
  (testing "returns bigdec with 3 digit precision"
    (is (= (bigdec 3.10)
           (ratio 1550650 500050))))
  (testing "returns nil if divisor is nil"
    (is (nil? (ratio nil 1.55))))
  (testing "returns nil if dividend is nil"
    (is (nil? (ratio 1.55 nil))))
  (testing "returns nil if dividend is zero"
    (is (nil? (ratio 1.55 0)))))

