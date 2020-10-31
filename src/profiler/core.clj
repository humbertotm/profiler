(ns profiler.core
  (:gen-class)
  (:require [profiler.operations.core :as profiler]
            [profiler.initialization.core :as init]
            [clojure.tools.logging :as log]))

(defn -main
  "Execute full profiling task"
  [& args]
  (init/initialize-caches)
  (log/info "Starting full profiling task")
  (profiler/execute-full-profiling)
  (log/info "Done!"))

