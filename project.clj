(defproject words "1.0.0-SNAPSHOT"
  :description "Word game server"
  :main words.core
  :dependencies [
                 [org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [compojure "0.6.4"][ring-json-params "0.1.0"]
                 [mysql/mysql-connector-java "5.1.6"]
                 [ring-basic-authentication "0.0.1"]
                 [commons-lang "2.3"]
                 [congomongo "0.1.7"]]
  :dev-dependencies [[lein-ring "0.4.5"]]
  :ring {:handler words.rest/app})
