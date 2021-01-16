(ns profiler.core
  (:gen-class)
  (:require [profiler.operations.core :as profiler]
            [profiler.initialization.core :as init]
            [mongodb.operations :as mdbops]
            [clojure.tools.logging :as log]))

(defn -main
  "Execute full profiling task"
  [& args]
  (log/info "Clearing profiles collection in profiler db")
  (mdbops/clear-coll "profiles")
  (init/initialize-caches)
  (log/info "Starting full profiling task")
  (profiler/execute-full-profiling)
  (log/info "Done!"))

