(ns helpers.core
  (:require [db.operations :as db-ops]
            [cache.core :refer [reset-cache]]))

(defn reset-test-cache
  [target-cache]
  (reset-cache target-cache 3))

