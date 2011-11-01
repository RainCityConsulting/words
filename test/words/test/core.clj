(ns words.test.core
  (:use [clojure.test])
  (:require
    [words.service :as service]
    [words.rules.standard :as standard]))

(deftest inserts
  (let [app-id (service/create-application "test-001" "http://test.com/001")]
    (println (str "application-id: " app-id))
    (let [player-id (service/create-player app-id "Ian")]
      (println (str "player-id: " player-id))
      (let [rule-id (service/create-rule "words.rules.standard" "Standard rules")]
        (println (str "rule-id: " rule-id))
        (let [game-id (service/create-game app-id rule-id)]
          (println (str "game-id: " game-id)
          (service/create-play game-id player-id "{\"pass\":true}")
          (is 1 (count (service/find-plays-by-game game-id)))
          (service/create-play game-id player-id "{\"pass\":true}"))
          (is 2 (count (service/find-plays-by-game game-id))))))))

(deftest std-rules
  (is 4 (:count (first (filter #(and (= "a" (:value %)) (= "c" (:type %)))
    (standard/remaining-tiles [{:word [{:type "c" :value "a"}]}])))))
  (is 133 (standard/remaining-tile-count [{:word [{:type "c" :value "a"}]}])))
