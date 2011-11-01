(ns words.service
  (:import (org.apache.commons.lang RandomStringUtils))
  (:require [words.persistence :as p]))

(defn find-application-by-id [id]
  (p/find-by-id :applications id))

(defn create-application
  ([name url]
    (create-application {:name name :url url}))
  ([a]
  (p/insert :applications (assoc a :security-key (RandomStringUtils/randomAlphanumeric 64)))))

(defn create-player
  ([application-id name]
    (create-player {:application-id application-id :name name}))
  ([p]
    (p/insert :players p)))

(defn find-player-by-id [id]
  (p/find-by-id :players id))

(defn find-player-by-id [id]
  (p/find-by-id :players id))

(defn create-rule
  ([name description]
    (create-rule {:name name :description description}))
  ([r]
    (p/insert :rules r)))

(defn find-rule-by-id [id]
  (p/find-by-id :rules id))

(defn find-rule-by-game-id [id]
  (p/find-rule-by-game-id id))

(defn create-game
  ([application-id rule-id]
    (create-game {:rule-id rule-id :application-id application-id}))
  ([g]
    (p/insert :games g)))

(defn find-game-by-id [id]
  (p/find-by-id :games id))

(defn play [game-id player-id play]
  (let [rule (find-rule-by-game-id game-id)]))

(defn create-play
  ([game-id player-id play]
    (create-play {:game-id game-id :player-id player-id :play play}))
  ([p]
    (p/insert :plays p)))

(defn find-play-by-id [id]
  (p/find-by-id :plays id))

(defn find-plays-by-game [id]
  (p/find-plays-by-game id))
