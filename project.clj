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
                 [org.clojure/core.cache "0.8.1"]
                 [org.clojure/core.async "1.3.610"]
                 [org.clojure/tools.trace "0.7.10"]
                 [environ "1.1.0"]]
  :plugins [[lein-environ "1.1.0"]] ; Don't know if this is the right place to keep plugins
  :main ^:skip-aot screener.core
  :target-path "target/%s"
  :test-paths ["test"]
  :profiles {:uberjar {:aot :all}
             :user {:plugins [[cider/cider-nrepl "0.24.0"]]} ; Place somewhere else
             :dev {:env {:db-subname "//localhost:5432/screener_dev"
                         :db-user "screeneruser"
                         :db-password "screeneruser"}}
             :test {:env {:db-subname "//localhost:5432/screener_test"
                          :db-user "screeneruser"
                          :db-password "screeneruser"}}})
