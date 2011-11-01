(ns words.rules)

(defprotocol Rules
  "Defines game play"
  (play [game-id player-id json-repr]))
