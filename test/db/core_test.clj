(ns db.core-test
  (:require [clojure.test :refer :all]
            [db.core :refer :all]))

(deftest test-db-spec
  (testing "spec returns the db spec map"
    (is (= spec
           {:classname "org.postgresql.Driver"
            :subprotocol "postgresql"
            :subname "//localhost:5432/screener_dev"
            :user "screeneruser"
            :password "screeneruser"}))))

