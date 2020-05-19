(ns screener.models.tables)

(def data-dependencies {:num [:sub :tag]})

(def data-type-to-table-map {:ticker :tickers
                             :sub :submissions
                             :tag :tags
                             :num :numbers
                             :pre :presentations})

