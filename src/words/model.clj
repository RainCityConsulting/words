(ns words.model
    (:require [clojure.java.jdbc :as sql]))

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

(defn insert-application [a]
  (transaction
    (sql/insert-record :applications a)))

(defn find-application-by-id [id]
  (transaction
    (sql/with-query-results rs ["SELECT * FROM applications WHERE id = ?" id] (first rs))))
