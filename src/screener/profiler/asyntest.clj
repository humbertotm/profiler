(ns screener.profiler.asyntest
  (:require [clojure.core.async :as async :refer [>! >!! <! <!! thread]]
            [screener.data.tickers :as tickers]))

(def tickers-atom (atom []))

(def tickers-list (list "a" "aa" "aaau" "aaba" "aacg" "aach" "aacqu" "aagh" "aaidx" "aal" "aamc"))


(defn threaded-ticker-retrieval
  "Threaded retrieval of ticker records from db. Order of final writing to target atom
  is not guaranteed but that is not a problem. It works."
  []
  (let [batch-size 2]
    (loop [partitioned-seq (partition batch-size batch-size nil tickers-list)]
      (when (not (empty? (first partitioned-seq)))
        (do (println "starting batch looping...")
            (loop [batch-seq (first partitioned-seq)]
              (when (not (nil? (first batch-seq)))
                (thread (println (str "will write " (first batch-seq) " after sleep"))
                        (Thread/sleep 1000)
                        (println (str "writing " (first batch-seq) " to tickers-atom"))
                        (swap! tickers-atom conj
                               (tickers/fetch-ticker-cik-mapping (first batch-seq))))
                (recur (rest batch-seq)))))
        (recur (rest partitioned-seq))))))

