;; TODO: figure out if requiring the record namespaces is needed in order for the macro
;; to work. Otherwise, clean this shit up.
(ns etl.records.core
  (:require [etl.records.number :refer :all]
            [etl.records.tag :refer :all]
            [etl.records.presentation :refer :all]
            [etl.records.submission :refer :all]))

(def records-map {:sub "submission"
                  :num "number"
                  :tag "tag"
                  :pre "presentation"})

(defmacro create-record
  "Returns an expression to be evaluated to create a record of type record-type from src-map"
  [record-type src-map]
  `((~resolve (~symbol (~str "screener.models." (~records-map ~record-type) "/create-" (~records-map ~record-type)))) ~src-map))

