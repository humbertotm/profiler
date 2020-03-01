(ns screener.models.value-setters
  (:require [java-time :as jtime]))

(defn string-or-nil
  "Returns lambda that returns a string value if present or nil"
  []
  #(if (not (empty? %)) % nil))

(defn number-or-nil
  "Returns lambda that returns a number value from string if present or nil"
  []
  #(if (not (empty? %))
     (try (read-string %)
          (catch Exception e
            (do (println (str "Invalid number: " %))
                nil)))
     nil))

(defn date-or-nil
  "Returns lambda that returns date value from string if present or nil"
  []
  #(if (not (empty? %1)) (jtime/local-date %2 %1) nil))

(defn datetime-or-nil
  "Returns lambda that returns datetime value from string if present or nil"
  []
  #(if (not (empty? %1)) (jtime/local-date-time %2 %1) nil))

(defn boolean-or-nil
  "Returns a lambda that returns a boolean value from a string being either 1 or 0"
  []
  #(if (not (empty? %))
     (if (= (read-string %) 1) true false)
     nil))

