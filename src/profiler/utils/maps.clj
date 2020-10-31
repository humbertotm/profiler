(ns profiler.utils.maps)

;; This function is kept for visualization purposes when working in the repl. Not employed
;; in the profiling process
(defn sort-profile-map-chron
  "Returns a sorted-by-year profile map"
  [profiles-map]
  (into (sorted-map-by (fn [k1 k2] (compare k1 k2)))
        profiles-map))

