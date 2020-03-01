(ns etl.core
  (:require [clojure.spec.alpha :as s]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [screener.models.core :as models :refer :all]
            [db.operations :as db-ops]
            [clojure.string :as str]))

(defn get-type-from-file-name
  "Infers data type from file name in file-path and returns it as a symbol"
  [file-path]
  (-> (str/split file-path #"\.")
      (first)
      (str/split #"/")
      (last)
      (keyword)))

(defn write-to-table
  "Loops over records sequence writing to target table"
  [target-table records]
  (if (first records)
    (do (if (and (list? (first records)) (= (first (first records)) :validation-error))
          (println (str "Invalid record: " (first records)))
          (try (db-ops/insert target-table (first records))
               (catch org.postgresql.util.PSQLException e
                 (println (str "Error while writing: " e)))
               (catch Exception e
                 (println (str "Some other unexpected error: " e)))))
        (recur target-table (rest records)))
    (println (str "Done writing to " target-table))))

(defn get-target-table
  "Returns the corresponding db table name as a symbol for the provided data type"
  [data-type]
  (models/tables data-type))

(defn csv-data->maps
  "Maps a csv line into a keyworded map based off the column names defined in the first row"
  [csv-data]
  (map zipmap
       (->> (first csv-data)
            (map keyword)
            repeat)
       (rest csv-data)))

(defn maps->Records
  "Creates list of Records of type record-type from a list of data-maps"
  [record-type data-maps]
  (map #(try (models/create-record record-type %)
             (catch Exception e
               (do (println (str "Error in " record-type %))
                   (println e)
                   (list :validation-error %))))
       data-maps))

;; Main ETL method
(defn etl
  "Extracts data from csv data source in specified path, transforms it into adhoc records
  and finally loads into into the corresponding db tables"
  [file-path]
  (let [data-type (get-type-from-file-name file-path)]
    (with-open [reader (io/reader file-path)]
      (try (->> (csv/read-csv reader :separator \tab :quote \')
                (csv-data->maps)
                (maps->Records data-type)
                (write-to-table (get-target-table data-type)))))))

