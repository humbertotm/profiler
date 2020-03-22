(ns screener.models.num-test
  (:require [clojure.test :refer :all]
            [screener.models.num :refer :all]))

(defn create-num-fixture
  [f]
  (def test-num {:ddate #inst "2018-04-30T00:00:00.000-00:00",
                 :value 0.0000M,
                 :adsh "0001625376-19-000017",
                 :footnote nil,
                 :uom "USD",
                 :coreg nil,
                 :tag "EntityPublicFloat",
                 :version "dei/2014",
                 :qtrs 0}))

(use-fixtures :each create-num-fixture)

;; TODO
;; (deftest test-create-num
;;   (testing "successfully creates Num record"
;;     ()))

(deftest test-create-num-cache-entry-key
  (testing "cache key for sub is keyworded adsh|tag|version"
    (is (=
         (create-num-cache-entry-key test-num)
         (keyword (str
                   (test-num :adsh) "|"
                   (test-num :tag) "|"
                   (test-num :version)))))))

