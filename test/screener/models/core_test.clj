(ns screener.models.core-test
  (:require [clojure.test :refer :all]
            [screener.models.tables :refer :all]))

(deftest test-data-to-table-mappings
  (testing "data-type-to-table-map"
    (is (= data-type-to-table-map {:ticker :tickers
                                   :sub :submissions
                                   :tag :tags
                                   :num :numbers
                                   :pre :presentations}))))

