(ns screener.utils.maps)

(defn sort-profile-map-chron
  [profiles-map]
  (into (sorted-map-by (fn [k1 k2] (compare k1 k2)))
        profiles-map))

