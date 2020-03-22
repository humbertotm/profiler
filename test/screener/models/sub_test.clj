(ns screener.models.sub-test
  (:require [clojure.test :refer :all]
            [screener.models.sub :refer :all]))

(defn create-sub-fixture
  [f]
  (def test-sub {:nciks "1",
                 :fye "1231",
                 :aciks nil,
                 :stprinc "DE",
                 :filed #inst "2019-08-07T00:00:00.000-00:00",
                 :baph "713-881-3600",
                 :instance "ae-20190630_htm.xml",
                 :cik "2178",
                 :countryba "US",
                 :ein "741753147",
                 :bas1 "17 S. BRIAR HOLLOW LN.",
                 :name "ADAMS RESOURCES & ENERGY, INC.",
                 :former "ADAMS RESOURCES & ENERGY INC",
                 :adsh "0000002178-19-000086",
                 :countryma "US",
                 :accepted #inst "2019-08-07T17:21:00.000000000-00:00",
                 :prevrpt false,
                 :bas2 nil,
                 :zipma "77001",
                 :countryinc "US",
                 :stprba "TX",
                 :mas2 nil,
                 :cityba "HOUSTON",
                 :changed #inst "1992-07-03T00:00:00.000-00:00",
                 :zipba "77027",
                 :cityma "HOUSTON",
                 :mas1 "P O BOX 844",
                 :fy "2019",
                 :form "10-Q",
                 :period #inst "2019-06-30T00:00:00.000-00:00",
                 :sic "5172",
                 :afs "2-ACC",
                 :wksi false,
                 :stprma "TX",
                 :fp "Q2",
                 :detail true}))


(use-fixtures :each create-sub-fixture)

;; TODO
;; (deftest test-create-sub
;;   (testing "successfully creates Sub record"
;;     ()))

(deftest test-create-sub-cache-entry-key
  (testing "cache key for sub is keyworded adsh"
    (is (= (create-sub-cache-entry-key test-sub) (keyword (test-sub :adsh))))))

