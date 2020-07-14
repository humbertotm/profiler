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

(def build-args-test-numbers
  (list {:ddate ((date-or-nil) "20190630" "yyyyMMdd"),
         :value 745000000.0000M,
         :adsh "0000002178-19-000087",
         :footnote nil,
         :uom "USD",
         :coreg nil,
         :tag
         "NetIncomeLoss",
         :version "us-gaap/2019",
         :qtrs 0},
        {:ddate ((date-or-nil) "20190630" "yyyyMMdd"),
         :value 45000000.0000M,
         :adsh "0000002178-19-000087",
         :footnote nil,
         :uom "USD",
         :coreg nil,
         :tag
         "Assets",
         :version "us-gaap/2019",
         :qtrs 0},
        {:ddate ((date-or-nil) "20190630" "yyyyMMdd"),
         :value 8450000.0000M,
         :adsh "0000002178-19-000087",
         :footnote nil,
         :uom "USD",
         :coreg nil,
         :tag
         "Goodwill",
         :version "us-gaap/2019",
         :qtrs 0},
        {:ddate ((date-or-nil) "20190630" "yyyyMMdd"),
         :value 1045000000.0000M,
         :adsh "0000002178-19-000087",
         :footnote nil,
         :uom "USD",
         :coreg nil,
         :tag
         "AssetsCurrent",
         :version "us-gaap/2019",
         :qtrs 0},
        {:ddate ((date-or-nil) "20190630" "yyyyMMdd"),
         :value 645000000.0000M,
         :adsh "0000002178-19-000087",
         :footnote nil,
         :uom "USD",
         :coreg nil,
         :tag
         "LiabilitiesCurrent",
         :version "us-gaap/2019",
         :qtrs 0}))

(def build-args-test-numbers-1
  (list {:ddate ((date-or-nil) "20190630" "yyyyMMdd"),
         :value 750000000.0000M,
         :adsh "0000002178-19-000088",
         :footnote nil,
         :uom "USD",
         :coreg nil,
         :tag
         "NetIncomeLoss",
         :version "us-gaap/2019",
         :qtrs 0},
        {:ddate ((date-or-nil) "20190630" "yyyyMMdd"),
         :value 45500000.0000M,
         :adsh "0000002178-19-000088",
         :footnote nil,
         :uom "USD",
         :coreg nil,
         :tag
         "Assets",
         :version "us-gaap/2019",
         :qtrs 0},
        {:ddate ((date-or-nil) "20190630" "yyyyMMdd"),
         :value 20400000.0000M,
         :adsh "0000002178-19-000088",
         :footnote nil,
         :uom "USD",
         :coreg nil,
         :tag
         "Goodwill",
         :version "us-gaap/2019",
         :qtrs 0},
        {:ddate ((date-or-nil) "20190630" "yyyyMMdd"),
         :value 1055000000.0000M,
         :adsh "0000002178-19-000088",
         :footnote nil,
         :uom "USD",
         :coreg nil,
         :tag
         "AssetsCurrent",
         :version "us-gaap/2019",
         :qtrs 0},
        {:ddate ((date-or-nil) "20190630" "yyyyMMdd"),
         :value 648000000.0000M,
         :adsh "0000002178-19-000088",
         :footnote nil,
         :uom "USD",
         :coreg nil,
         :tag
         "LiabilitiesCurrent",
         :version "us-gaap/2019",
         :qtrs 0}))

(def adp-numbers
  "Defined as the output of calling (seq {}) to guarantee order of the list."
  (list [:2010
         (list {:ddate ((date-or-nil) "20100630" "yyyyMMdd"),
                :value 745000000.0000M,
                :adsh "0000002178-19-000077",
                :footnote nil,
                :uom "USD",
                :coreg nil,
                :tag
                "NetIncomeLoss",
                :version "us-gaap/2010",
                :qtrs 0}
               {:ddate ((date-or-nil) "20100630" "yyyyMMdd"),
                :value 45000000.0000M,
                :adsh "0000002178-19-000077",
                :footnote nil,
                :uom "USD",
                :coreg nil,
                :tag
                "Assets",
                :version "us-gaap/2010",
                :qtrs 0}
               {:ddate ((date-or-nil) "20100630" "yyyyMMdd"),
                :value 8450000.0000M,
                :adsh "0000002178-19-000077",
                :footnote nil,
                :uom "USD",
                :coreg nil,
                :tag
                "Goodwill",
                :version "us-gaap/2010",
                :qtrs 0}
               {:ddate ((date-or-nil) "20100630" "yyyyMMdd"),
                :value 1045000000.0000M,
                :adsh "0000002178-19-000077",
                :footnote nil,
                :uom "USD",
                :coreg nil,
                :tag
                "AssetsCurrent",
                :version "us-gaap/2010",
                :qtrs 0}
               {:ddate ((date-or-nil) "20100630" "yyyyMMdd"),
                :value 645000000.0000M,
                :adsh "0000002178-19-000077",
                :footnote nil,
                :uom "USD",
                :coreg nil,
                :tag
                "LiabilitiesCurrent",
                :version "us-gaap/2010",
                :qtrs 0})]
        [:2011
         (list {:ddate ((date-or-nil) "20110630" "yyyyMMdd"),
                :value 725000000.0000M,
                :adsh "0000002178-19-000078",
                :footnote nil,
                :uom "USD",
                :coreg nil,
                :tag
                "NetIncomeLoss",
                :version "us-gaap/2011",
                :qtrs 0}
               {:ddate ((date-or-nil) "20110630" "yyyyMMdd"),
                :value 43000000.0000M,
                :adsh "0000002178-19-000078",
                :footnote nil,
                :uom "USD",
                :coreg nil,
                :tag
                "Assets",
                :version "us-gaap/2011",
                :qtrs 0}
               {:ddate ((date-or-nil) "20110630" "yyyyMMdd"),
                :value 8250000.0000M,
                :adsh "0000002178-19-000078",
                :footnote nil,
                :uom "USD",
                :coreg nil,
                :tag
                "Goodwill",
                :version "us-gaap/2011",
                :qtrs 0}
               {:ddate ((date-or-nil) "20110630" "yyyyMMdd"),
                :value 1025000000.0000M,
                :adsh "0000002178-19-000078",
                :footnote nil,
                :uom "USD",
                :coreg nil,
                :tag
                "AssetsCurrent",
                :version "us-gaap/2011",
                :qtrs 0}
               {:ddate ((date-or-nil) "20110630" "yyyyMMdd"),
                :value 625000000.0000M,
                :adsh "0000002178-19-000078",
                :footnote nil,
                :uom "USD",
                :coreg nil,
                :tag
                "LiabilitiesCurrent",
                :version "us-gaap/2011",
                :qtrs 0})]
        [:2012
         (list {:ddate ((date-or-nil) "20120630" "yyyyMMdd"),
                :value 730000000.0000M,
                :adsh "0000002178-19-000079",
                :footnote nil,
                :uom "USD",
                :coreg nil,
                :tag
                "NetIncomeLoss",
                :version "us-gaap/2012",
                :qtrs 0}
               {:ddate ((date-or-nil) "20120630" "yyyyMMdd"),
                :value 43500000.0000M,
                :adsh "0000002178-19-000079",
                :footnote nil,
                :uom "USD",
                :coreg nil,
                :tag
                "Assets",
                :version "us-gaap/2012",
                :qtrs 0}
               {:ddate ((date-or-nil) "20120630" "yyyyMMdd"),
                :value 8300000.0000M,
                :adsh "0000002178-19-000079",
                :footnote nil,
                :uom "USD",
                :coreg nil,
                :tag
                "Goodwill",
                :version "us-gaap/2012",
                :qtrs 0}
               {:ddate ((date-or-nil) "20120630" "yyyyMMdd"),
                :value 1030000000.0000M,
                :adsh "0000002178-19-000079",
                :footnote nil,
                :uom "USD",
                :coreg nil,
                :tag
                "AssetsCurrent",
                :version "us-gaap/2012",
                :qtrs 0}
               {:ddate ((date-or-nil) "20120630" "yyyyMMdd"),
                :value 630000000.0000M,
                :adsh "0000002178-19-000079",
                :footnote nil,
                :uom "USD",
                :coreg nil,
                :tag
                "LiabilitiesCurrent",
                :version "us-gaap/2012",
                :qtrs 0})]
        [:2013
         (list {:ddate ((date-or-nil) "20130630" "yyyyMMdd"),
                :value 735000000.0000M,
                :adsh "0000002178-19-000080",
                :footnote nil,
                :uom "USD",
                :coreg nil,
                :tag
                "NetIncomeLoss",
                :version "us-gaap/2013",
                :qtrs 0}
               {:ddate ((date-or-nil) "20130630" "yyyyMMdd"),
                :value 44000000.0000M,
                :adsh "0000002178-19-000080",
                :footnote nil,
                :uom "USD",
                :coreg nil,
                :tag
                "Assets",
                :version "us-gaap/2013",
                :qtrs 0}
               {:ddate ((date-or-nil) "20130630" "yyyyMMdd"),
                :value 8350000.0000M,
                :adsh "0000002178-19-000080",
                :footnote nil,
                :uom "USD",
                :coreg nil,
                :tag
                "Goodwill",
                :version "us-gaap/2013",
                :qtrs 0}
               {:ddate ((date-or-nil) "20130630" "yyyyMMdd"),
                :value 1035000000.0000M,
                :adsh "0000002178-19-000080",
                :footnote nil,
                :uom "USD",
                :coreg nil,
                :tag
                "AssetsCurrent",
                :version "us-gaap/2013",
                :qtrs 0}
               {:ddate ((date-or-nil) "20130630" "yyyyMMdd"),
                :value 635000000.0000M,
                :adsh "0000002178-19-000080",
                :footnote nil,
                :uom "USD",
                :coreg nil,
                :tag
                "LiabilitiesCurrent",
                :version "us-gaap/2013",
                :qtrs 0})]
        [:2014
         (list {:ddate ((date-or-nil) "20140630" "yyyyMMdd"),
                :value 740000000.0000M,
                :adsh "0000002178-19-000081",
                :footnote nil,
                :uom "USD",
                :coreg nil,
                :tag
                "NetIncomeLoss",
                :version "us-gaap/2014",
                :qtrs 0}
               {:ddate ((date-or-nil) "20140630" "yyyyMMdd"),
                :value 44500000.0000M,
                :adsh "0000002178-19-000081",
                :footnote nil,
                :uom "USD",
                :coreg nil,
                :tag
                "Assets",
                :version "us-gaap/2014",
                :qtrs 0}
               {:ddate ((date-or-nil) "20140630" "yyyyMMdd"),
                :value 8400000.0000M,
                :adsh "0000002178-19-000081",
                :footnote nil,
                :uom "USD",
                :coreg nil,
                :tag
                "Goodwill",
                :version "us-gaap/2014",
                :qtrs 0}
               {:ddate ((date-or-nil) "20140630" "yyyyMMdd"),
                :value 1040000000.0000M,
                :adsh "0000002178-19-000081",
                :footnote nil,
                :uom "USD",
                :coreg nil,
                :tag
                "AssetsCurrent",
                :version "us-gaap/2014",
                :qtrs 0}
               {:ddate ((date-or-nil) "20140630" "yyyyMMdd"),
                :value 640000000.0000M,
                :adsh "0000002178-19-000081",
                :footnote nil,
                :uom "USD",
                :coreg nil,
                :tag
                "LiabilitiesCurrent",
                :version "us-gaap/2014",
                :qtrs 0})]
        [:2015 (list)]
        [:2016 (list)]
        [:2017 (list)]
        [:2018 (list)]
        [:2019 (list)]))

(def avt-numbers (list [:2010 (list)]
                       [:2011 (list)]
                       [:2012 (list)]
                       [:2013 (list)]
                       [:2014 (list)]
                       [:2015 (list)]
                       [:2016 (list)]
                       [:2017 (list)]
                       [:2018 (list)]
                       [:2019 (list)]))

