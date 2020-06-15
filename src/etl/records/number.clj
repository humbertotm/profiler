(ns etl.records.number
  (:require [etl.records.utils.validations :refer :all]
            [etl.records.utils.value-setters :refer :all]))

(defrecord Number
    [adsh
     tag
     version
     coreg
     ddate
     qtrs
     uom
     value
     footnote])

(defn create-number
  "Creates a new Num record from a map with all-string values from csv"
  [number]
  {:pre [(s/valid? :unq/number number)]}
  (let [{:keys [adsh
                tag
                version
                coreg
                ddate
                qtrs
                uom
                value
                footnote]} number]
    (->Number ((string-or-nil) adsh)
              ((string-or-nil) tag)
              ((string-or-nil) version)
              ((string-or-nil) coreg)
              ((date-or-nil) ddate "yyyyMMdd")
              ((number-or-nil) qtrs)
              ((string-or-nil) uom)
              ((number-or-nil) value)
              ((string-or-nil) footnote))))

