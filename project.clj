(defproject LD33 "0.1.0-SNAPSHOT"
  :description "My entry for LD33"
  :url "https://github.com/achesnais/LD33"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.48"]]
  :plugins [[lein-cljsbuild "1.0.5"]
            [lein-figwheel "0.3.7"]]
  :hooks [leiningen.cljsbuild]
  :cljsbuild
  {:builds {:main
            {:source-paths ["src/"]
             :figwheel true
             :compiler {:main "game.core"
                        :asset-path "js/out"
                        :output-to "resources/public/js/game.js"
                        :output-dir "resources/public/js/out"}}
            :final
            {:source-paths ["src/"]
             :figwheel false
             :compiler {:main "game.core"
                        :asset-path "js/out"
                        :output-to "target/game.js"
                        :output-dif "target/js/out"}}}}
  :figwheel
  {:http-server-root "public"
   :server-pot 3449
   :server-ip "0.0.0.0"
   :nrepl-port 7888})
