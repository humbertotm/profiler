(ns screener.profiler.asyntest
  (:require [clojure.core.async :as async :refer [>! >!! <! <!! thread]]
            [cache.core :as cache]
            [screener.data.tickers :as tickers]))

(defn initialize-test-cache
  []
  (cache/create-fifo-cache test-cache {} 2))

(import java.util.concurrent.CountDownLatch)

(def tickers-atom (atom []))

(def tickers-list (list "a" "aa" "aaau" "aaba" "aacg" "aach" "aacqu" "aagh" "aaidx" "aal" "aamc"))

;; TODO: experiment a batched threaded execution with countdown latches.
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

(defn threaded-batched-ticker-retrieval
  "POC for batched threaded retrieval and write to cache of records. Following this flow
  we can be sure that cache can be kept in sync with whatever batch size makes sense (
  probably same number as db connection pool)."
  []
  (let [batch-size 2]
    (loop [partitioned-seq (partition batch-size batch-size nil tickers-list)]
      (when (not (empty? (first partitioned-seq)))
        (do (println "starting batch looping...")
            (let [n (if (= batch-size (count (first partitioned-seq)))
                      batch-size
                      (count (first partitioned-seq)))
                  latch (java.util.concurrent.CountDownLatch. n)]
              (loop [batch-seq (first partitioned-seq)]
                (when (not (nil? (first batch-seq)))
                  (thread
                    (println (str "will write " (first batch-seq) " after sleep"))
                    (Thread/sleep 1000)
                    (println (str "Writing: " (first batch-seq)))
                    (cache/fetch-cacheable-data test-cache
                                                (keyword (first batch-seq))
                                                tickers/retrieve-mapping)
                    (.countDown latch))
                  (recur (rest batch-seq))))
              (.await latch)))
        (recur (rest partitioned-seq))))))

