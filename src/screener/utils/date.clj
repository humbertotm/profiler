(ns screener.utils.date
  (:require [clojure.string :as str]))

(defn extract-year
  "Expects a java.sql.Date instance. Will convert to string and return the year value as a
   string."
  [date-object]
  (let [date-string (.toString date-object)]
    (first (str/split date-string #"-"))))

