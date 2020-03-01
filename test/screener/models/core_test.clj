(ns screener.models.core-test
  (:require [clojure.test :refer :all]
            [screener.models.core :refer :all]))

(deftest test-numbers
  (testing "screener.models/tables"
    (is (= tables {:sub :submissions
                   :tag :tags
                   :num :numbers
                   :pre :presentations}))))

