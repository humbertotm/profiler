(ns screener.utils.async
  (:import [java.util.concurrent CountDownLatch])
  (:require [clojure.core.async :as async :refer [thread]]))

(defn n-threads-exec
  "Executes provided function exec-fn in n-threads concurrently in a latched fashion.
  exec-fn must be a function that receives a single parameter, this being each element in
  src-list consumed individually."
  [src-list n-threads exec-fn]
  (loop [partitioned-src-list (partition n-threads n-threads nil src-list)]
    (when (not (empty? (first partitioned-src-list)))
      (let [n (if (= n-threads (count (first partitioned-src-list)))
                n-threads
                (count (first partitioned-src-list)))
            latch (CountDownLatch. n)]
        (loop [batch (first partitioned-src-list)]
          (when (not (nil? (first batch)))
            (let [e (first batch)]
              (thread (exec-fn e)
                      (.countDown latch))
              (recur (rest batch)))))
        (.await latch))
      (recur (rest partitioned-src-list)))))

