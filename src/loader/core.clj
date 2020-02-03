(ns loader.core)
(require '[clojure.data.csv :as csv]
         '[clojure.java.io :as io]
         '[screener.models :as models]
         '[db.operations :as db-ops]
         '[clojure.string :as str])

(defn load
  "Loads records to db from csv data source in specified path"
  [file-path]
  (let [data-type (get-type-from-file-name file-path)]
    (with-open [reader (io/reader file-path)]
      (->> (csv/read-csv reader)
           (csv-data->maps)
           (maps->Records data-type)
           (write-to-table (get-target-table data-type))))))

(defn csv-data->maps
  "Maps a csv line into a keyworded map based off the column names defined in the first row"
  [csv-data]
  (map zipmap
       (->> (first csv-data)
            (map keyword)
            repeat)
       (rest csv-data)))

(defn maps->Records
  "Transforms a collection of maps into a collection of records of the specified record-type"
  [record-type data-maps]
  (let [record-name (models/records-map record-type)]
    (map #((-> (str 'map '-> record-name) symbol resolve) %)
         data-maps)))

(defn write-to-table
  "Loops over records sequence writing to target-table"
  [target-table records]
  (if (first records)
    (recur (do (try (db-ops/insert target-table (first records))
                    (catch org.postgresql.util.PSQLException e
                      (println (str "Error while writing: " e)))))
           (next records))
    (println (str "Done writing to " target-table))))

(defn get-type-from-file-name
  "Infers data type from file name in file-path and returns it as a symbol"
  [file-path]
  (-> (str/split file-path #"\.")
      (first)
      (str/split #"/")
      (last)
      (keyword)))

(defn get-target-table
  "Returns the corresponding db table name as a symbol for the provided data type"
  [data-type]
  (models/tables data-type))

;; Testing crap

;; (defrecord Foo [foo bar])

;; (def test-map-vector
;;   (vector {:foo "foo0" :bar "bar0"} {:foo "foo1" :bar "bar1"} {:foo "foo2" :bar "bar2"}))

;; (def records-map {:foo "Foo"})

;; (defn maps->Records
;;   [record-type data-maps]
;;   (let [record-name (records-map record-type)]
;;     (map #((-> (str 'map '-> record-name) symbol resolve) %) data-maps)))

;; (defn log-to-console
;;   [target records]
;;   (if (first records)
;;     (recur (do (println (str (:foo (first records))))
;;                (println (str (:bar (first records)))))
;;            (next records))
;;     (println (str "Done writing to " target))))

