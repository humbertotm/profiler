(defproject profiler "0.1.0-SNAPSHOT"
  :description "profiler"
  :url ""
  :license {:name "Eclipse Public License"
            :url "https://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/java.jdbc "0.7.10"]
                 [org.clojure/data.json "1.0.0"]
                 [org.clojure/core.cache "0.8.1"]
                 [org.clojure/core.async "1.3.610"]
                 [org.clojure/tools.trace "0.7.10"]
                 [org.clojure/data.csv "0.1.4"]
                 [org.clojure/tools.logging "1.1.0"]
                 [org.postgresql/postgresql "42.2.8"]
                 [com.mchange/c3p0 "0.9.5.4"]
                 [com.novemberain/monger "3.1.0"]
                 [org.apache.logging.log4j/log4j-core "2.13.3"]
                 [clojure.java-time "0.3.2"]
                 [environ "1.2.0"]]
  :plugins [[lein-environ "1.2.0"]]
  :main ^:skip-aot profiler.core
  :target-path "target/%s"
  :test-paths ["test"]
  :jvm-opts ["-Dclojure.tools.logging.factory=clojure.tools.logging.impl/log4j2-factory"]
  :profiles {:uberjar {:aot :all}
             :user {:plugins [[cider/cider-nrepl "0.24.0"]]} ; Place somewhere else
             :dev [:project/dev :profiles/dev]
             :test [:project/test :profiles/test]
             :profiles/dev {}
             :profiles/test {}
             :project/dev {:source-paths ["src" "tool-src"]
                           :dependencies [[midje "1.6.3"]]
                           :plugins [[lein-auto "0.1.3"]]}
             :project/test {}})
