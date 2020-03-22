(ns screener.models.core
  (:require [screener.models.num :refer :all]
            [screener.models.tag :refer :all]
            [screener.models.pre :refer :all]
            [screener.models.sub :refer :all]
            [screener.models.cik-ticker-mapping :refer :all]))

(def records-map {:sub "sub"
                  :tag "tag"
                  :num "num"
                  :pre "pre"})

(def tables {:cik-ticker :cik-ticker-mappings
             :sub :submissions
             :tag :tags
             :num :numbers
             :pre :presentations})

(defmacro create-record
  "Returns an expression to be evaluated to create a record of type record-type from src-map"
  [record-type src-map]
  `((~resolve (~symbol (~str "screener.models." (~records-map ~record-type) "/create-" (~records-map ~record-type)))) ~src-map))

