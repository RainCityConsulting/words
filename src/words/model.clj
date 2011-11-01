(ns words.model
  (:use clojure.contrib.condition)
  (:require [clojure.contrib.sql :as sql]))

(def db {
    :classname "com.mysql.jdbc.Driver"
    :subprotocol "mysql"
    :subname "//localhost/words_dev"
    :user "words_user"
    :password "words"})

(defmacro wrap-connection [& body]
  `(if (sql/find-connection)
    ~@body
    (sql/with-connection db ~@body)))

(defmacro transaction [& body]
  `(if (sql/find-connection)
     (sql/transaction ~@body)
     (sql/with-connection db (sql/transaction ~@body))))

(defn last-insert-id []
  (transaction
    (sql/with-query-results rs ["SELECT LAST_INSERT_ID() AS id"] (:id (first rs)))))

(defn insert-player [p]
  (transaction
    (sql/insert-records :players p)
    (last-insert-id)))

(defn find-player-by-id [id]
  (transaction
    (sql/with-query-results rs ["SELECT * FROM players WHERE id = ?" id]
      (or (first rs) (raise :type :not-found :message (format "Unknown ID [%s]" id))))))

(defn insert-application [a]
  (transaction
    (sql/insert-records :applications a)
    (last-insert-id)))

(defn insert-application-key [k]
  (transaction
    (sql/insert-records :application_keys k)))

(defn find-application-by-id [id]
  (transaction
    (sql/with-query-results rs ["SELECT * FROM applications WHERE id = ?" id]
      (or (first rs) (raise :type :not-found :message (format "Unknown ID [%s]" id))))))

(defn find-application-key-by-application [id]
  (transaction
    (sql/with-query-results rs ["SELECT * FROM application_keys WHERE application_id = ?" id]
      (or (first rs) (raise :type :not-found :message (format "Unknown ID [%s]" id))))))
