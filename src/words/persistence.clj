(ns words.persistence
  (:use clojure.contrib.condition)
  (:require [somnium.congomongo :as mongo]))

(def db (mongo/make-connection :words :host "127.0.0.1" :port 27017))

(defn insert [collection a]
  (mongo/with-mongo db
    (. (:_id (mongo/insert! collection a)) toString)))

(defn find-by-id [collection id]
  (mongo/with-mongo db
    (mongo/fetch-by-id collection (mongo/object-id id))))

(defn find-rule-by-game-id [id]
  (mongo/with-mongo db
    (let [rule-id (:rule-id (mongo/fetch :games :where {:game-id id}))]
      (find-by-id :rules rule-id))))

(defn find-plays-by-game [id]
  (mongo/with-mongo db
    (mongo/fetch :plays :where {:game-id id} :sort {:_id})))
