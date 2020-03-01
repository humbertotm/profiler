(ns screener.models.num
  (:require [clojure.spec.alpha :as s]
            [screener.models.value-setters :refer :all]
            [screener.models.validations :refer :all]))

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
  [num]
  {:pre [(s/valid? :unq/num num)]}
  (let [{:keys [adsh
                tag
                version
                coreg
                ddate
                qtrs
                uom
                value
                footnote]} num]
    (->Num ((string-or-nil) adsh)
           ((string-or-nil) tag)
           ((string-or-nil) version)
           ((string-or-nil) coreg)
           ((date-or-nil) ddate "yyyyMMdd")
           ((number-or-nil) qtrs)
           ((string-or-nil) uom)
           ((number-or-nil) value)
           ((string-or-nil) footnote))))

(def test-nums [{:adsh "someadsh"
                 :tag "sometag"
                 :version "someversion"
                 :coreg "4515"
                 :ddate "20200205"
                 :uom "someuom"}
                {:adsh "someadsh"
                 :tag "sometag"
                 :version "someversion"
                 :coreg "4515"
                 :qtrs "2"
                 :ddate "20200205"
                 :uom "someuom"}])


