(ns words.rest
  (:use compojure.core)
  (:use clojure.contrib.condition)
  (:use ring.middleware.json-params)
  (:use ring.middleware.lint)
  (:use ring.util.response)
  (:import
    (java.io PrintWriter)
    (clojure.contrib.condition Condition))
  (:require
    [words.service :as service]
    [compojure.route :as route]
    [compojure.handler :as handler]
    [compojure.response :as response]
    [clojure.contrib.json :as json]))

(defn- write-java-sql-timestamp-json [x #^PrintWriter out]
  (.print out (str "\"" x "\"")))

;; clojure 1.3
;;(defn- write-java-sql-timestamp-json [x #^PrintWriter out escape-unicode?]
;;  (.print out (str x)))

(defn json-response [data & [status]]
  {:status (or status 200)
    :headers {"Content-Type" "application/json"}
    :body (json/json-str data)})

(defn wrap-error-handling [handler]
  (fn [req]
    (try
      (or
        (handler req)
        (json-response {"error" "resource not found"} 404))
      (catch Condition e
        (let [{:keys [type message]} (meta e)]
          (json-response {"error" message}))))))

(extend java.sql.Timestamp json/Write-JSON
  {:write-json write-java-sql-timestamp-json})

(defn create-player [req]
  (redirect (str "/players/" (service/create-player (:json-params req)))))

(defn show-player [id]
  (json-response (service/find-player-by-id id)))

(defn create-application [req]
  (redirect (str "/applications/" (service/create-application (:json-params req)))))

(defn show-application [id]
  (json-response (service/find-application-by-id id)))

(defroutes main-routes
  (GET "/applications/:id" [id] (show-application id))
  (PUT "/applications" [:as request] (create-application request))
  (GET "/players/:id" [id] (show-player id))
  (PUT "/players" [:as request] (create-player request))
  )

(def app
  (-> main-routes wrap-lint wrap-json-params wrap-error-handling))
