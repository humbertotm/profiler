(ns etl.records.presentation
  (:require [etl.records.utils.value-setters :refer :all]
            [etl.records.utils.validations :refer :all]
            [clojure.spec.alpha :as s]))

(defrecord Presentation
    [adsh
     report
     line
     stmt
     inpth
     rfile
     tag
     version
     plabel])

(defn create-presentation
  [presentation]
  {:pre [(s/valid? :unq/presentation presentation)]}
  (let [{:keys [adsh
                report
                line
                stmt
                inpth
                rfile
                tag
                version
                plabel]} presentation]
    (->Presentation ((string-or-nil) adsh)
                    ((string-or-nil) report)
                    ((number-or-nil) line)
                    ((string-or-nil) stmt)
                    ((boolean-or-nil) inpth)
                    ((string-or-nil) rfile)
                    ((string-or-nil) tag)
                    ((string-or-nil) version)
                    ((string-or-nil) plabel))))

