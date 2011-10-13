(ns words.rest
  (:use
    compojure.core)
  (:import
    (java.io PrintWriter))
  (:require
    [words.model :as model]
    [compojure.route :as route]
    [compojure.handler :as handler]
    [compojure.response :as response]
    [clojure.data.json :as json]))

(defn- write-java-sql-timestamp-json [x #^PrintWriter out escape-unicode?]
  (.print out (str x)))

(extend java.sql.Timestamp json/Write-JSON
  {:write-json write-java-sql-timestamp-json})

(defn show-application [id]
  (json/json-str (model/find-application-by-id id)))

(defroutes main-routes
  (GET "/applications/:id" [id] (show-application id)))

(def app
  (handler/site main-routes))
