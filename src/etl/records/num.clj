(ns etl.records.num
  (:require [etl.records.utils.validations :refer :all]
            [etl.records.utils.value-setters :refer :all]
            [clojure.spec.alpha :as s]))

(defrecord Num
    [adsh
     tag
     version
     coreg
     ddate
     qtrs
     uom
     value
     footnote])

(defn create-num
  "Creates a new Num record from a map with all-string values from csv"
  [number]
  {:pre [(s/valid? :unq/num number)]}
  (let [{:keys [adsh
                tag
                version
                coreg
                ddate
                qtrs
                uom
                value
                footnote]} number]
    (->Num ((string-or-nil) adsh)
           ((string-or-nil) tag)
           ((string-or-nil) version)
           ((string-or-nil) coreg)
           ((date-or-nil) ddate "yyyyMMdd")
           ((number-or-nil) qtrs)
           ((string-or-nil) uom)
           ((number-or-nil) value)
           ((string-or-nil) footnote))))

