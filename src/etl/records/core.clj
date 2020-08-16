;; TODO: figure out if requiring the record namespaces is needed in order for the macro
;; to work. Otherwise, clean this shit up.
(ns etl.records.core
  (:require [etl.records.num :refer :all]
            [etl.records.tag :refer :all]
            [etl.records.presentation :refer :all]
            [etl.records.submission :refer :all]))

(def records-map {:sub "submission"
                  :num "num"
                  :tag "tag"
                  :pre "presentation"})

(def record-table {:sub "submissions"
                   :num "numbers"
                   :tag "tags"
                   :pre "presentations"})

(defmacro create-record
  "Returns an expression to be evaluated to create a record of type record-type from src-map"
  [record-type src-map]
  `((~resolve (~symbol (~str "etl.records." (~records-map ~record-type) "/create-" (~records-map ~record-type)))) ~src-map))

