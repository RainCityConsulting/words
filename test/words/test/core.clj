(ns words.test.core
  (:use [words.core :as core])
  (:use [words.model :as model])
  (:use [clojure.test])
  (:require [clojure.java.jdbc :as sql]))

(deftest test-insert-application
  (model/transaction
    (sql/set-rollback-only)
    (let [appId (:generated_key (model/insert-application {:name "__test" :url "http://test.com"}))]
      (is (< 0 appId))
      (is (= "__test" (:name (find-application-by-id appId)))))))
