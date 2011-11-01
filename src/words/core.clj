(ns words.core
  (:gen-class)
  (:require
    [words.service :as service]
    [words.rules.standard :as standard]))

(defn -main [& args]
  (let [app-id (service/create-application "test-001" "http://test.com/001")]
    (println (str "application-id: " app-id))
    (let [player-id (service/create-player app-id "Ian")]
      (println (str "player-id: " player-id))
      (let [rule-id (service/create-rule "words.rules.standard" "Standard rules")]
        (println (str "rule-id: " rule-id))
        (let [game-id (service/create-game app-id rule-id)]
          (println (str "game-id: " game-id)))))))
