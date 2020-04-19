(defproject screener "0.1.0-SNAPSHOT"
  :description "screener"
  :url ""
  :license {:name "Eclipse Public License"
            :url "https://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/java.jdbc "0.7.10"]
                 [org.postgresql/postgresql "42.2.8"]
                 [com.mchange/c3p0 "0.9.5.4"]
                 [org.clojure/data.csv "0.1.4"]
                 [clojure.java-time "0.3.2"]
                 [org.clojure/core.cache "0.8.1"]]
  :main ^:skip-aot screener.core
  :target-path "target/%s"
  :test-paths ["test"]
  :profiles {:uberjar {:aot :all}})
