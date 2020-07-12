(ns screener.calculations.operations
  (:require [screener.data.tickers :as tickers]
            [screener.data.num :as num]
            [screener.data.sub :as sub]))

(defn ratio
  "A simple ratio calculation that does some minimal validation on inputs. Returns a nil
   value if ratio cannot be calculated.
   Formats return value to display two decimal places."
  [divisor dividend]
  (if (or (nil? divisor)
          (or (nil? dividend)
              (zero? dividend)))
    nil
    (with-precision 3 (/ (bigdec divisor) (bigdec dividend)))))

