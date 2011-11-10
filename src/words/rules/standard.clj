(ns words.rules.standard
  (:require
    [words.rules :as rules]
    [words.service :as service]
    [clojure.set :as set]
    [clojure.contrib.json :as json]
    [clojure.contrib.math :as math]))

;;(defrecord Cell [attrs tile])
;;(defrecord Play [type origin orientation word])
;;(defprotocol Tile)

(def char-tile-points
     {
      "a" 1,
      "b" 4,
      "c" 4,
      "d" 2,
      "e" 1,
      "f" 4,
      "g" 3,
      "h" 4,
      "i" 1,
      "j" 10,
      "k" 5,
      "l" 1,
      "m" 4,
      "n" 2,
      "o" 1,
      "p" 4,
      "q" 10,
      "r" 1,
      "s" 1,
      "t" 1,
      "u" 2,
      "v" 5,
      "w" 4,
      "x" 8,
      "y" 3,
      "z" 10})

(def all-tiles
  [
    {:type :char :char "a" :count 5}
    {:type :char :char "b" :count 5}
    {:type :char :char "c" :count 5}
    {:type :char :char "d" :count 5}
    {:type :char :char "e" :count 5}
    {:type :char :char "f" :count 5}
    {:type :char :char "g" :count 5}
    {:type :char :char "h" :count 5}
    {:type :char :char "i" :count 5}
    {:type :char :char "j" :count 5}
    {:type :char :char "k" :count 5}
    {:type :char :char "l" :count 5}
    {:type :char :char "m" :count 5}
    {:type :char :char "n" :count 5}
    {:type :char :char "o" :count 5}
    {:type :char :char "p" :count 5}
    {:type :char :char "q" :count 5}
    {:type :char :char "r" :count 5}
    {:type :char :char "s" :count 5}
    {:type :char :char "t" :count 5}
    {:type :char :char "u" :count 5}
    {:type :char :char "v" :count 5}
    {:type :char :char "w" :count 5}
    {:type :char :char "x" :count 5}
    {:type :char :char "y" :count 5}
    {:type :char :char "z" :count 5}
    {:type "b" :count 2}])

(defn tile-value [tile]
  (if (= :char (:type tile))
    (get char-tile-points (:char tile))
    0))

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

(defn tiles-equal? [a b]
  (or
    (= "b" (:type a) (:type b))
    (and
      (= :char (:type a) (:type b))
      (= (:char a) (:char b)))))

(defn remove-tile [flat-tiles t]
  ((fn [ts acc]
    (if (tiles-equal? (first ts) t)
      (conj acc (rest ts))
      (recur (rest ts) (conj acc (first ts))))) flat-tiles []))

(defn random-tiles [n tiles]
  ((fn [n ts acc]
       (if (or (= 0 n) (empty? tiles))
         acc
         (let [t (random-tile ts)]
           (recur (dec n) (remove-tile ts t) (conj acc t))))) n tiles []))

;; Board
;; A board is a 2-d vector of maps
;; Each map can contain the following keys
;;
;; :double-word
;; :triple-word
;; :double-letter
;; :triple-letter
;; :star
;;
;; :tile {:type :char :points}
;; :player-id
;; :points
;; :word-points
;; :uses [{:player-id :points :word-points}]
;;

(def special-cells
  {:double-word [[1 5] [1 9] [3 7] [5 1] [5 13] [7 3] [7 11] [9 1] [9 13] [11 7] [13 5] [13 9]]
   :triple-word [[0 3] [0 11] [3 0] [3 14] [11 0] [11 14] [14 3] [14 11]]
   :double-letter [[1 2] [1 12]
                   [2 1] [2 4] [2 10] [2 13]
                   [4 2] [4 6] [4 8] [4 12]
                   [6 4] [6 10]
                   [8 4] [8 10]
                   [10 2] [10 6] [10 8] [10 12]
                   [12 1] [12 4] [12 10] [12 13]
                   [13 2] [13 12]]
   :triple-letter [[0 6] [0 8]
                   [3 3] [3 11]
                   [5 5] [5 9]
                   [6 0] [6 14]
                   [8 0] [8 14]
                   [9 5] [9 9]
                   [11 3] [11 11]
                   [14 6] [14 8]]
   :star [[7 7]]})

(defn zip-special-cells []
  (mapcat concat (map (fn [[k v]] (map #(conj (vector %) k) v)) (into [] special-cells))))

(defmulti play-coords :orientation)

(defmethod play-coords :horizontal [play]
  (map-indexed (fn [idx t] [(+ idx (first (:origin play))) (second (:origin play))]) (:word play)))

(defmethod play-coords :vertical [play]
  (map-indexed (fn [idx t] [(first (:origin play)) (+ idx (second (:origin play)))]) (:word play)))

(defn empty-board
  ([n] (empty-board n n))
  ([rows cols] (->> (repeat cols {}) vec (repeat rows) vec)))

(defn update-board-cell [board coords f & args]
  (apply update-in board coords f args))

(defn zip-play-coords [play]
  (map vector (play-coords play) (:word play)))

(defn board-cell-contains? [board k [x y]]
  (k (get-in board [y x])))

;; TODO Rename this or hide it in a letfn or something like that.
(defn calc-tile-points [board [x y] points]
  (cond
   (board-cell-contains? board :double-letter [x y]) (* 2 points)
   (board-cell-contains? board :triple-letter [x y]) (* 3 points)
   :default points))

(defn tile-points [board {:keys [coords] :as tile}]
  (let [points (tile-value tile)]
    (if (board-cell-contains? board :tile coords)
      points
      (calc-tile-points board coords points))))

;; TODO Rename this or hide it in a letfn or something like that.
(defn word-points-adjust [board [x y] points]
  (cond
   (board-cell-contains? board :tile [x y]) points
   (board-cell-contains? board :double-word [x y]) (* 2 points)
   (board-cell-contains? board :triple-word [x y]) (* 3 points)
   :default points))

(defn word-points
  "Calculates total points for the given word played on the given board.
  word is a sequence of maps of :coords :tile. The board is a representation
  of the board before the word is played."
  [board word]
  (reduce #(word-points-adjust board (:coords %2) %1)
	  (reduce #(+ %1 (tile-points board %2)) 0 word)
	  word))

(defn h-word [board [x y :as coords]]
  (if (and
       (board-cell-contains? board :tile coords)
       (or (= 0 x) (not (board-cell-contains? board :tile [(dec x) y])))
       (board-cell-contains? board :tile [(inc x) y]))
    (loop [x1 x word []]
      (if-let [tile (board-cell-contains? board :tile [x1 y])]
        (recur (inc x1) (conj word (merge tile {:coords [x1 y]})))
        word))
    []))

(defn v-word [board [x y :as coords]]
  (if (and
       (board-cell-contains? board :tile coords)
       (or (= 0 y) (not (board-cell-contains? board :tile [x (dec y)])))
       (board-cell-contains? board :tile [x (inc y)]))
    (loop [y1 y word []]
      (if-let [tile (board-cell-contains? board :tile [x y1])]
        (recur (inc y1) (conj word (merge tile {:coords [x y1]})))
        word))
    []))

(defn all-board-coords [board]
  (sort (for [x (range (count board)) y (range (count (first board)))] [x y])))

(defn all-words [board]
  (filter seq
          (reduce concat []
		  (map (juxt (partial h-word board) (partial v-word board))
		       (all-board-coords board)))))

(defn apply-play-tile [board coords tile tile-value tile-points]
  (update-board-cell board coords assoc :tile tile :tile-value tile-value :tile-points tile-points))

(defn apply-play [board play]
  (letfn [(apt-intermediate [board [[x y] tile]]
            (apply-play-tile board [y x] tile (tile-value tile) (tile-points board (assoc tile :coords [x y]))))]
    (reduce apt-intermediate board (zip-play-coords play))))

(defn play-points [board play]
  (reduce #(+ %1 (word-points board %2)) 0
	  (set/difference (set (all-words (apply-play board play))) (set (all-words board)))))

(defn apply-special-cell [board coords k]
  (update-board-cell board coords assoc k true))

(defn apply-special-cells [board]
  (letfn [(asc-intermediate [board [coords k]]
            (apply-special-cell board coords k))]
    (reduce asc-intermediate board (zip-special-cells))))

(defn is-coord-in-bounds? [board coord]
  (and
    (>= (first coord) 0)
    (>= (second coord) 0)
    (< (first coord) (count (first board)))
    (< (second coord) (count board))))

(defn is-play-in-bounds? [board play]
  (every? (partial is-coord-in-bounds? board) (play-coords play)))

(defn all-board-cells [board]
  (reduce concat [] board))

(defn board-contains-any? [board k]
  (some (partial board-cell-contains? board k) (all-board-coords board)))

(defn is-adjacent? [[x1 y1] [x2 y2]]
  (or
    (and (= x1 x2) (= 1 (math/abs (- y1 y2))))
    (and (= y1 y2) (= 1 (math/abs (- x1 x2))))))

(defn all-board-coords-containing [board k]
  (filter (partial board-cell-contains? board k) (all-board-coords board)))

(defn do-coords-overlap? [coords1 coords2]
  (seq (set/intersection (set coords1) (set coords2))))

(defn adjacent-coords [coords1 coords2]
  (seq (for [c1 coords1 c2 coords2 :when (is-adjacent? c1 c2)] [c1 c2])))

(defn is-valid-play? [board play]
  (and
   (is-play-in-bounds? board play)
   (not (do-coords-overlap? (all-board-coords-containing board :tile) (play-coords play)))
   (adjacent-coords (all-board-coords-containing board :tile) (play-coords play))))
