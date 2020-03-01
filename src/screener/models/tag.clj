(ns screener.models.tag
  (:require [clojure.spec.alpha :as s]
            [screener.models.value-setters :refer :all]
            [screener.models.validations :refer :all]))

(defrecord Tag
  [tag
   version
   custom
   abstract
   datatype
   iord
   crdr
   tlabel
   doc])

(defn create-tag
  [tag]
  {:pre [(s/valid? :unq/tag tag)]}
  (let [{:keys [tag
                version
                custom
                abstract
                datatype
                iord
                crdr
                tlabel
                doc]} tag]
    (->Tag ((string-or-nil) tag)
           ((string-or-nil) version)
           ((boolean-or-nil) custom)
           ((boolean-or-nil) abstract)
           ((string-or-nil) datatype)
           ((string-or-nil) iord)
           ((string-or-nil) crdr)
           ((string-or-nil) tlabel)
           ((string-or-nil) doc))))

