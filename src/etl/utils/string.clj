(ns etl.utils.string)

(defn pluralize
  "Simplistically appends an s to the end of the provided string. Meant to be used only as a
   way to compute table names from data type name"
  [word]
  (str word "s"))

