(ns fixtures.num
  (:require [etl.records.utils.value-setters :refer :all]))

(def sub-adams-10k-2019-nums
  (list {:ddate ((date-or-nil) "20190630" "yyyyMMdd"),
         :value 745000000.0000M,
         :adsh "0000002178-19-000087",
         :footnote nil,
         :uom "USD",
         :coreg nil,
         :tag
         "NetIncome",
         :version "us-gaap/2019",
         :qtrs 0},
        {:ddate ((date-or-nil) "20190630" "yyyyMMdd"),
         :value 1333000000.0000M,
         :adsh "0000002178-19-000087",
         :footnote nil,
         :uom "USD",
         :coreg nil,
         :tag "AccountsReceivableNetCurrent",
         :version "us-gaap/2019",
         :qtrs 0},
        {:ddate ((date-or-nil) "20190630" "yyyyMMdd"),
         :value 727000000.0000M,
         :adsh "0000002178-19-000087",
         :footnote nil,
         :uom "USD",
         :coreg nil,
         :tag "AccruedLiabilitiesCurrent",
         :version "us-gaap/2019",
         :qtrs 0}))

