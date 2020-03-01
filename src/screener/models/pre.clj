(ns screener.models.pre
  (:require [clojure.spec.alpha :as s]
            [screener.models.value-setters :refer :all]
            [screener.models.validations :refer :all]))

(defrecord Pre
    [adsh
     report
     line
     stmt
     inpth
     rfile
     tag
     version
     plabel])

(defn create-pre
  [pre]
  {:pre [(s/valid? :unq/pre pre)]}
  (let [{:keys [adsh
                report
                line
                stmt
                inpth
                rfile
                tag
                version
                plabel]} pre]
    (->Pre ((string-or-nil) adsh)
           ((string-or-nil) report)
           ((number-or-nil) line)
           ((string-or-nil) stmt)
           ((boolean-or-nil) inpth)
           ((string-or-nil) rfile)
           ((string-or-nil) tag)
           ((string-or-nil) version)
           ((string-or-nil) plabel))))

