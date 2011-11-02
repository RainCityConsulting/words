(ns words.rules.standard
  (:require
    [words.rules :as rules]
    [words.service :as service]
    [clojure.contrib.json :as json]))

;;(defrecord Cell [attrs tile])
;;(defrecord Play [type origin orientation word])
;;(defprotocol Tile)

(def char-tile-points
  [
    {:char "a" :points 1}
    {:char "b" :points 1}
    {:char "c" :points 1}
    {:char "d" :points 1}
    {:char "e" :points 1}
    {:char "f" :points 1}
    {:char "g" :points 1}
    {:char "h" :points 1}
    {:char "i" :points 1}
    {:char "j" :points 1}
    {:char "k" :points 1}
    {:char "l" :points 1}
    {:char "m" :points 1}
    {:char "n" :points 1}
    {:char "o" :points 1}
    {:char "p" :points 1}
    {:char "q" :points 1}
    {:char "r" :points 1}
    {:char "s" :points 1}
    {:char "t" :points 1}
    {:char "u" :points 1}
    {:char "v" :points 1}
    {:char "w" :points 1}
    {:char "x" :points 1}
    {:char "y" :points 1}
    {:char "z" :points 1}])

(def all-tiles
  [
    {:type "c" :char "a" :count 5}
    {:type "c" :char "b" :count 5}
    {:type "c" :char "c" :count 5}
    {:type "c" :char "d" :count 5}
    {:type "c" :char "e" :count 5}
    {:type "c" :char "f" :count 5}
    {:type "c" :char "g" :count 5}
    {:type "c" :char "h" :count 5}
    {:type "c" :char "i" :count 5}
    {:type "c" :char "j" :count 5}
    {:type "c" :char "k" :count 5}
    {:type "c" :char "l" :count 5}
    {:type "c" :char "m" :count 5}
    {:type "c" :char "n" :count 5}
    {:type "c" :char "o" :count 5}
    {:type "c" :char "p" :count 5}
    {:type "c" :char "q" :count 5}
    {:type "c" :char "r" :count 5}
    {:type "c" :char "s" :count 5}
    {:type "c" :char "t" :count 5}
    {:type "c" :char "u" :count 5}
    {:type "c" :char "v" :count 5}
    {:type "c" :char "w" :count 5}
    {:type "c" :char "x" :count 5}
    {:type "c" :char "y" :count 5}
    {:type "c" :char "z" :count 5}
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
    (map #(if (= (:char %) (:char t)) (assoc % :count (dec (:count %))) %) tiles)))

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
      (= (:char a) (:char b)))))

(defn random-tiles [n tiles]
  ((fn [n ts acc]
       (if (or (= 0 n) (empty? tiles))
         acc
         (let [t (random-tile ts)]
           (recur (dec n) (remove-tile ts t) (conj acc t))))) n tiles []))

;; Board
;; A board is a 2-d vector of sets
;; Each map can contain the following keys
;;
;; :double-word
;; :triple-word
;; :double-letter
;; :triple-letter
;; :star
;;
;; :play {:tile {:type :char :points} :player-id :tile-points :word-points}
;;

(defmulti play-coords :orientation)

(defmethod play-coords :horizontal [play]
  (map-indexed (fn [idx t] [(second (:origin play)) (+ idx (first (:origin play)))]) (:word play)))

(defmethod play-coords :vertical [play]
  (map-indexed (fn [idx t] [(+ idx (second (:origin play))) (first (:origin play))]) (:word play)))

(defn is-star [[x y]]
  (= 8 x y))

(defn is-valid-play? [plays play]
  (if (empty? plays)
    (some is-star (play-coords play))
    true))

(defn empty-board []
  (vec (repeat 15 (vec (repeat 15 {})))))

(defn zip-play-coords [play]
  (map vector (play-coords play) (:word play)))

(defn apply-play-tile [board play-tile]
  (apply assoc-in board play-tile))

(defn apply-play [board play]
  (reduce apply-play-tile board (zip-play-coords play)))

(defn fill-board [board plays]
  (sort
    #()
    ((fn [ts [p & ps]]
       (if (not p)
         ts
         (recur (concat ts ) ps))) [] plays)))
