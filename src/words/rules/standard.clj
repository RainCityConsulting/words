(ns words.rules.standard
  (:require
    [words.rules :as rules]
    [words.service :as service]
    [clojure.contrib.json :as json]))

(def char-tile-points
  [
    {:value "a" :points 1}
    {:value "b" :points 1}
    {:value "c" :points 1}
    {:value "d" :points 1}
    {:value "e" :points 1}
    {:value "f" :points 1}
    {:value "g" :points 1}
    {:value "h" :points 1}
    {:value "i" :points 1}
    {:value "j" :points 1}
    {:value "k" :points 1}
    {:value "l" :points 1}
    {:value "m" :points 1}
    {:value "n" :points 1}
    {:value "o" :points 1}
    {:value "p" :points 1}
    {:value "q" :points 1}
    {:value "r" :points 1}
    {:value "s" :points 1}
    {:value "t" :points 1}
    {:value "u" :points 1}
    {:value "v" :points 1}
    {:value "w" :points 1}
    {:value "x" :points 1}
    {:value "y" :points 1}
    {:value "z" :points 1}])

(def all-tiles
  [
    {:type "c" :value "a" :count 5}
    {:type "c" :value "b" :count 5}
    {:type "c" :value "c" :count 5}
    {:type "c" :value "d" :count 5}
    {:type "c" :value "e" :count 5}
    {:type "c" :value "f" :count 5}
    {:type "c" :value "g" :count 5}
    {:type "c" :value "h" :count 5}
    {:type "c" :value "i" :count 5}
    {:type "c" :value "j" :count 5}
    {:type "c" :value "k" :count 5}
    {:type "c" :value "l" :count 5}
    {:type "c" :value "m" :count 5}
    {:type "c" :value "n" :count 5}
    {:type "c" :value "o" :count 5}
    {:type "c" :value "p" :count 5}
    {:type "c" :value "q" :count 5}
    {:type "c" :value "r" :count 5}
    {:type "c" :value "s" :count 5}
    {:type "c" :value "t" :count 5}
    {:type "c" :value "u" :count 5}
    {:type "c" :value "v" :count 5}
    {:type "c" :value "w" :count 5}
    {:type "c" :value "x" :count 5}
    {:type "c" :value "y" :count 5}
    {:type "c" :value "z" :count 5}
    {:type "b" :count 2 :points 0}])

(deftype StandardRules []
  rules/Rules
  (play [game-id player-id json-repr]
    ;; Need to validate play and return new tiles
    ;; JSON representation must have
    ;; start {x, y}
    ;; end {x, y}
    ;; word [{type, value}...}
    ;;
    ;; OR
    ;;
    ;; exchange
    ;;
    ;; OR
    ;;
    ;; pass
    (let [json (json/read-json json-repr)]
      (service/create-play game-id player-id json-repr))))

(defn remove-tile [tiles t]
  (filter #(< 0 (:count %))
    (map #(if (= (:value %) (:value t)) (assoc % :count (dec (:count %))) %) tiles)))

(defn remove-tiles-for-play [play tiles]
  (if-let [word (:word play)]
    (reduce remove-tile tiles word)
    tiles))

(defn remaining-tiles [plays]
  (letfn [(rem-t [plays acc]
            (if (empty? plays)
              acc
              (rem-t (rest plays) (remove-tiles-for-play (first plays) acc))))]
    (rem-t plays all-tiles)))

(defn remaining-tile-count [tiles]
  (reduce + (map #(:count %) tiles)))

(defn flatten-tiles [tiles]
  ((fn inner [ts acc]
    (if (empty? ts)
      acc
      ((fn [t n acc]
        (if (= n 0)
          (inner (rest ts) acc)
          (recur t (dec n) (conj acc t)))) (first ts) (:count (first ts)) acc))) tiles []))

(defn random-tile [flat-tiles]
  (rand-nth flat-tiles))

(defn remove-tile [flat-tiles t]
  ((fn [ts acc]
    (if (tiles-equal? (first ts) t)
      (conj acc (rest ts))
      (recur (rest ts) (conj acc (first ts))))) flat-tiles []))

(defn tiles-equal? [a b]
  (or
    (= "b" (:type a) (:type b))
    (and
      (= "c" (:type a) (:type b))
      (= (:value a) (:value b)))))

(defn random-tiles [n tiles]
  ((fn [n ts acc]
       (if (or (= 0 n) (empty? tiles))
         acc
         (let [t (random-tile ts)]
           (recur (dec n) (remove-tile ts t) (conj acc t))))) n tiles []))

;;;; Board
;; A board is a 2-d vector of sets
;; Each map can contain the following keys
;;
;; :double-word
;; :triple-word
;; :double-letter
;; :triple-letter
;; :star
;;
;; :play {:tile {:type :value :points} :player-id :tile-points :word-points}
;;

(def double-word-positions [[1 5] [1 9]])

(defn empty-board
  [
    [#{} #{} #{} #{:triple-word} #{} #{} #{:triple-letter} #{} #{:triple-letter} #{} #{} #{:triple-word} #{} #{} #{}]
    [#{} #{} #{:double-letter} #{} #{} #{:double-word} #{} #{} #{} #{:double-word} #{} #{} #{:double-letter} #{} #{}]
    [#{} #{:double-letter} #{} #{} #{:double-letter} #{} #{} #{} #{} #{} #{:double-letter} #{} #{} #{:double-letter} #{}]
    [#{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{}]
    [#{} #{} #{:double-letter} #{} #{} #{} #{:double-letter} #{} #{:double-letter} #{} #{} #{} #{:double-letter} #{} #{}]
    [#{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{}]
    [#{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{}]
    [#{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{}]
    [#{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{}]
    [#{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{}]
    [#{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{}]
    [#{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{}]
    [#{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{}]
    [#{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{}]
    [#{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{} #{}]
  ])
