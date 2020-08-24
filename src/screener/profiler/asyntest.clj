(ns screener.profiler.asyntest
  (:require [clojure.core.async :as async :refer [>! >!! <! <!! thread]]))

(def test-atom (atom []))

;; TODO: this seems to be working for my purposes. Test this by retrievnig data from tickers
;; table and writing to test atom.
(defn threaded-writing
  ""
  []
  (let [test-vector [1 2 3 4 5 6 7 8 9 10]
        batch-size 2]
    (loop [partitioned-seq (partition batch-size batch-size nil test-vector)]
      (when (not (empty? (first partitioned-seq)))
        (do (println "starting batch looping...")
            (loop [batch-seq (first partitioned-seq)]
              (when (not (nil? (first batch-seq)))
                (thread (Thread/sleep (* 1000 (- 10 (first batch-seq))))
                        (println (str "writing " (first batch-seq) " to test-atom"))
                        (swap! test-atom conj (first batch-seq)))
                (recur (rest batch-seq)))))
        (recur (rest partitioned-seq))))))

(defn simple-loop
  ""
  []
  (let [test-vector [1 2 3 4 5 6]]
    (loop [i 0]
      (when (not (nil? (first (subvec test-vector i (count test-vector)))))
        (println (first (subvec test-vector i (count test-vector))))
        (recur (inc i))))))

