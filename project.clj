(defproject words "1.0.0-SNAPSHOT"
  :description "Word game server"
  :dependencies [
      [org.clojure/clojure "1.3.0"]
      [org.clojure/java.jdbc "0.0.6"]
      [org.clojure/data.json "0.1.1"]
      [compojure "0.6.4"]
      [mysql/mysql-connector-java "5.1.6"]]
  :dev-dependencies [[lein-ring "0.4.5"]]
  :ring {:handler words.rest/app})
