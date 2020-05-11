(ns fixtures.sub
  (:require [screener.models.value-setters :refer :all]))

(def sub-adams-10q {:nciks "1",
                    :fye "1231",
                    :aciks nil,
                    :stprinc "DE",
                    :filed ((date-or-nil) "20190807" "yyyyMMdd"),
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
                    :accepted ((datetime-or-nil) "2019-08-07 10:20:30.5" "yyyy-MM-dd HH:mm:ss.S"),
                    :prevrpt false,
                    :bas2 nil,
                    :zipma "77001",
                    :countryinc "US",
                    :stprba "TX",
                    :mas2 nil,
                    :cityba "HOUSTON",
                    :changed ((date-or-nil) "20190807" "yyyyMMdd"),
                    :zipba "77027",
                    :cityma "HOUSTON",
                    :mas1 "P O BOX 844",
                    :fy "2019",
                    :form "10-Q",
                    :period ((date-or-nil) "20190807" "yyyyMMdd"),
                    :sic "5172",
                    :afs "2-ACC",
                    :wksi false,
                    :stprma "TX",
                    :fp "Q2",
                    :detail true})

(def sub-adams-10k-2019 {:nciks "1",
                         :fye "1231",
                         :aciks nil,
                         :stprinc "DE",
                         :filed ((date-or-nil) "20190807" "yyyyMMdd"),
                         :baph "713-881-3600",
                         :instance "ae-20190630_htm.xml",
                         :cik "2178",
                         :countryba "US",
                         :ein "741753147",
                         :bas1 "17 S. BRIAR HOLLOW LN.",
                         :name "ADAMS RESOURCES & ENERGY, INC.",
                         :former "ADAMS RESOURCES & ENERGY INC",
                         :adsh "0000002178-19-000087",
                         :countryma "US",
                         :accepted ((datetime-or-nil) "2019-12-07 10:20:30.5" "yyyy-MM-dd HH:mm:ss.S"),
                         :prevrpt false,
                         :bas2 nil,
                         :zipma "77001",
                         :countryinc "US",
                         :stprba "TX",
                         :mas2 nil,
                         :cityba "HOUSTON",
                         :changed ((date-or-nil) "20191207" "yyyyMMdd"),
                         :zipba "77027",
                         :cityma "HOUSTON",
                         :mas1 "P O BOX 844",
                         :fy "2019",
                         :form "10-K",
                         :period ((date-or-nil) "20191207" "yyyyMMdd"),
                         :sic "5172",
                         :afs "2-ACC",
                         :wksi false,
                         :stprma "TX",
                         :fp "Q4",
                         :detail true})

(def sub-adams-10k-2018 {:nciks "1",
                         :fye "1231",
                         :aciks nil,
                         :stprinc "DE",
                         :filed ((date-or-nil) "20180807" "yyyyMMdd"),
                         :baph "713-881-3600",
                         :instance "ae-20190630_htm.xml",
                         :cik "2178",
                         :countryba "US",
                         :ein "741753147",
                         :bas1 "17 S. BRIAR HOLLOW LN.",
                         :name "ADAMS RESOURCES & ENERGY, INC.",
                         :former "ADAMS RESOURCES & ENERGY INC",
                         :adsh "0000002178-19-000080",
                         :countryma "US",
                         :accepted ((datetime-or-nil) "2018-12-07 10:20:30.5" "yyyy-MM-dd HH:mm:ss.S"),
                         :prevrpt false,
                         :bas2 nil,
                         :zipma "77001",
                         :countryinc "US",
                         :stprba "TX",
                         :mas2 nil,
                         :cityba "HOUSTON",
                         :changed ((date-or-nil) "20181207" "yyyyMMdd"),
                         :zipba "77027",
                         :cityma "HOUSTON",
                         :mas1 "P O BOX 844",
                         :fy "2018",
                         :form "10-K",
                         :period ((date-or-nil) "20181207" "yyyyMMdd"),
                         :sic "5172",
                         :afs "2-ACC",
                         :wksi false,
                         :stprma "TX",
                         :fp "Q4",
                         :detail true})


