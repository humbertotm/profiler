(ns etl.core
  (:require [clojure.string :as str]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [etl.records.core :as records :refer :all]
            [etl.utils.string :refer :all]
            [db.operations :as db-ops])
  (:import [java.util.zip ZipEntry ZipOutputStream ZipInputStream]
           java.io.File
           java.lang.System))

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
  (map #(try (records/create-record record-type %)
             (catch AssertionError e
               (do (println (str "Error: " e))
                   (list :validation-error %))))
       data-maps))

(defn print-maps
  [data-maps]
  (loop [dmaps data-maps]
    (if (first dmaps)
      (do (println (first dmaps))
          (recur (rest dmaps)))
      (print "Done\n"))))

(defn print-records
  [data-records]
  (loop [drecs data-records]
    (if (first drecs)
      (do (println (first drecs))
          (recur (rest drecs)))
      (print "Done\n"))))

;; This whole process has been replaced by that in screener-content python repo.
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
                (write-to-table ((keyword data-type) records/record-table)))))))

;; TODO: NEED TO TRY THIS OUT AND DEBUG
;; (defn unzip-file
;;   ""
;;   [input output]
;;   (with-open [stream (-> input io/input-stream ZipInputStream.)]
;;     (loop [entry (.getNextEntry stream)]
;;       (if entry
;;         (let [base-path (str (System/getProperty "user.dir") File/separatorChar "data")
;;               file-name (.getName entry)
;;               save-path (str base-path File/separatorChar file-name)
;;               output-file (File. save-path)]
;;           (if (.isDirectory entry)
;;             (if-not (.exists output-file)
;;               (.mkdirs output-file)))
;;           (recur (.getNextEntry stream)))))))

