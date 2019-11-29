(defproject screener "0.1.0-SNAPSHOT"
  :description "screener"
  :url ""
  :license {:name "Eclipse Public License"
            :url "https://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/java.jdbc "0.7.10"]]
  :main ^:skip-aot screener.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
