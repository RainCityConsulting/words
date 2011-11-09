(ns words.test.rules.standard
  (:use [clojure.test])
  (:use [words.rules.standard]))

(def board (apply-special-cells (empty-board 15)))

(def play-1 {
             :origin [0 0],
             :orientation :horizontal,
             :word [
                    {:type :char, :char "c"}
                    {:type :char, :char "a"}
                    {:type :char, :char "t"}]})

(def play-2 {
             :origin [1 1],
             :orientation :horizontal,
             :word [
                    {:type :char, :char "b"}
                    {:type :char, :char "i"}
                    {:type :char, :char "n"}]})

(deftest new-board
  (is (= 4 (count (empty-board 4))))
  (is (= 4 (count (first (empty-board 4)))))
  (is (= 4 (count (empty-board 4 5))))
  (is (= 5 (count (first (empty-board 4 5))))))

(deftest board-bounds
  (is (is-coord-in-bounds? (empty-board 4) [0 0]))
  (is (is-coord-in-bounds? (empty-board 4) [3 3]))
  (is (is-coord-in-bounds? (empty-board 4) [0 3]))
  (is (is-coord-in-bounds? (empty-board 4) [3 0]))
  (is (not (is-coord-in-bounds? (empty-board 4) [-1 0])))
  (is (not (is-coord-in-bounds? (empty-board 4) [0 -1])))
  (is (not (is-coord-in-bounds? (empty-board 4) [4 0])))
  (is (not (is-coord-in-bounds? (empty-board 4) [0 4]))))

(deftest new-board-special-cells
  (is (= [7 7] (first (all-board-coords-containing board :star)))))

(deftest board-size
  (is (= (* 15 15) (count (all-board-cells board))))
  (is (= (* 15 15) (count (all-board-coords board)))))

(deftest adjacency
  (is (is-adjacent? [0 0] [0 1]))
  (is (is-adjacent? [0 0] [1 0]))
  (is (is-adjacent? [2 2] [2 1]))
  (is (is-adjacent? [2 2] [2 3]))
  (is (is-adjacent? [2 2] [1 2]))
  (is (is-adjacent? [2 2] [3 2]))
  (is (not (is-adjacent? [0 0] [0 0])))
  (is (not (is-adjacent? [0 0] [1 1])))
  (is (not (is-adjacent? [0 0] [0 2])))
  (is (not (is-adjacent? [0 0] [2 1]))))

(deftest plays
  (is (= 3 (count (play-coords play-1))))
  (is (= [[0 0] [1 0] [2 0]] (play-coords play-1)))
  (is (board-cell-contains? (apply-play board play-1) :tile [0 0]))
  (is (board-cell-contains? (apply-play board play-1) :tile [1 0]))
  (is (board-cell-contains? (apply-play board play-1) :tile [2 0]))
  (is (= 1 (count (all-words (apply-play board play-1)))))
  (is (= "cat" (reduce #(str %1 (:char %2)) "" (first (all-words (apply-play board play-1))))))
  (is (board-cell-contains? (apply-play (apply-play board play-1) play-2) :tile [1 1]))
  (is (board-cell-contains? (apply-play (apply-play board play-1) play-2) :tile [2 1]))
  (is (board-cell-contains? (apply-play (apply-play board play-1) play-2) :tile [3 1]))
  (is (is-origin-of-word? (apply-play (apply-play board play-1) play-2) [0 0]))
  (is (is-origin-of-word? (apply-play (apply-play board play-1) play-2) [1 0]))
  (is (is-origin-of-word? (apply-play (apply-play board play-1) play-2) [2 0]))
  (is (is-origin-of-word? (apply-play (apply-play board play-1) play-2) [1 1]))
  (is (= 4 (all-words (apply-play (apply-play board play-1) play-2)))))
