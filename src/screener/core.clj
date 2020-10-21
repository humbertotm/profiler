(ns screener.core
  (:gen-class)
  (:require [screener.profiler.core :as profiler]
            [screener.initialization.core :as init]
            [clojure.tools.logging :as log]))

(defn -main
  "Execute full profiling task"
  [& args]
  (init/initialize-caches)
  (log/info "Starting full profiling task")
  (profiler/execute-full-profiling)
  (log/info "Done!"))

