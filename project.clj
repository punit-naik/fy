(defproject fy "0.1.0-SNAPSHOT"
  :description "Fy Programming Challenge"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [clj-http "3.10.0"]
                 [metosin/reitit "0.3.10"]
                 [ring/ring-jetty-adapter "1.7.1"]]
  :aot :all
  :main fy.core
  :erpl-option {:init-ns fy.core})
