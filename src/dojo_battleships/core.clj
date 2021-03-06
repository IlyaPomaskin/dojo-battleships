(ns dojo-battleships.core
  (:require [clojure2d.core :refer :all])
  (:gen-class))

(def window-name "Battleships")
(def world-size 10)
(def field-size 30)
(def ships [5 4 4 3 3 2 2 2 1 1 1 1])
(def canvas-width (* world-size field-size))
(def canvas-height (* world-size field-size))

(defn render-hit [canvas cell]
  (-> canvas
      (set-color :white 160)
      (ellipse (+ (* field-size (:x cell)) (/ field-size 2))
               (+ (* field-size (:y cell)) (/ field-size 2))
               (/ field-size 2)
               (/ field-size 2))))
(defn render-ship [canvas cell]
  (-> canvas
      (set-color :blue 100)
      (rect (* field-size (:x cell))
            (* field-size (:y cell))
            field-size
            field-size)))
(defn render-hit-ship [canvas cell]
  (-> canvas
      (render-ship cell)
      (set-color :red 160)
      (ellipse (+ (* field-size (:x cell)) (/ field-size 2))
               (+ (* field-size (:y cell)) (/ field-size 2))
               (/ field-size 2)
               (/ field-size 2))))
(defn render-empty [canvas cell]
  (-> canvas
      (set-color :white 160)
      (rect (* field-size (:x cell))
            (* field-size (:y cell))
            field-size
            field-size
            true)))

(defn get-render-cell-fn [cell]
  (cond
    (and (:hit? cell) (:ship? cell)) render-hit-ship
    (:hit? cell) render-hit
    (:ship? cell) render-ship
    :else render-empty))

(defn render-board [canvas board]
  (mapv (fn [cell]
          (let [render-fn (get-render-cell-fn cell)]
            (render-fn canvas cell)))
        board))

(defn can-be-hit? [[x y] cell]
  (and (= (:x cell) x)
       (= (:y cell) y)
       (false? (:hit? cell))))

(defn hit-cell [coords state]
  (update-in
   state
   [(:current-player state) :board]
   (fn [board]
     (mapv
      #(if (can-be-hit? coords %1)
         (assoc %1 :hit? true)
         %1)
      board))))

(defn switch-player [state]
  (update-in
   state
   [:current-player]
   #(if (= %1 :player-1)
      :player-2
      :player-1)))

(defn hit [coords state]
  (let [board (get-in state [(:current-player state) :board])
        cell (first (filter #(can-be-hit? coords %1) board))]
    (if (can-be-hit? coords cell)
      (if (:ship? cell)
        (hit-cell coords state)
        (->> state
             (hit-cell coords)
             (switch-player)))
      state)))

(defmethod mouse-event [window-name :mouse-pressed] [event state]
  ;; do something when button is pressed
  (let [x (quot (.-x (.getPoint event)) field-size)
        y (quot (.-y (.getPoint event)) field-size)]
    (hit [x y] state)))

(defn draw
  [canvas
   window
   ^long framecount
   state]
  (let [global-state (get-state window)]
    (set-background canvas :lightblue 200)
    (render-board canvas (get-in global-state [(:current-player global-state) :board])))
  state)

(defn init-board [size]
  (vec (for [x (range 0 size)
             y (range 0 size)]
         {:x x :y y :hit? false :ship? false})))

(defn extreme-coord [ship coord extreme-fn]
  (apply extreme-fn (map coord ship)))
(defn near-cells [ship]
  (for [x (range (max 0 (dec (extreme-coord ship :x min))) (inc (min world-size (inc (extreme-coord ship :x max)))))
        y (range (max 0 (dec (extreme-coord ship :y min))) (inc (min world-size (inc (extreme-coord ship :y max)))))]
    {:x x :y y}))
(defn get-cell [board cell]
  (first (filter #(and (= (:x cell) (:x %)) (= (:y cell) (:y %)))
                 board)))
(defn cell-occupied? [board cell]
  (true? (:ship? (get-cell board cell))))
(defn ship-placement-is-valid? [board ship]
  (not-any? (partial cell-occupied? board) (near-cells ship)))

(def window (show-window {:canvas (canvas canvas-width canvas-height)
                          :window-name window-name
                          :fps 25
                          :draw-fn draw
                          :state {:state :game
                                  :current-player :player-1
                                  :player-1 {:setup-ships-left ships
                                             :board [{:x 0 :y 0 :hit? false :ship? true}
                                                     {:x 0 :y 1 :hit? false :ship? true}
                                                     {:x 0 :y 2 :hit? false :ship? true}
                                                     {:x 0 :y 3 :hit? false :ship? true}
                                                     {:x 0 :y 4 :hit? false :ship? false}
                                                     {:x 0 :y 5 :hit? false :ship? false}
                                                     {:x 0 :y 6 :hit? false :ship? false}
                                                     {:x 0 :y 7 :hit? false :ship? false}
                                                     {:x 0 :y 8 :hit? false :ship? false}
                                                     {:x 0 :y 9 :hit? false :ship? false}

                                                     {:x 1 :y 0 :hit? false :ship? false}
                                                     {:x 1 :y 1 :hit? false :ship? false}
                                                     {:x 1 :y 2 :hit? false :ship? false}
                                                     {:x 1 :y 3 :hit? false :ship? false}
                                                     {:x 1 :y 4 :hit? false :ship? false}
                                                     {:x 1 :y 5 :hit? false :ship? false}
                                                     {:x 1 :y 6 :hit? false :ship? false}
                                                     {:x 1 :y 7 :hit? false :ship? false}
                                                     {:x 1 :y 8 :hit? false :ship? false}
                                                     {:x 1 :y 9 :hit? false :ship? false}

                                                     {:x 2 :y 0 :hit? false :ship? true}
                                                     {:x 2 :y 1 :hit? false :ship? false}
                                                     {:x 2 :y 2 :hit? false :ship? false}
                                                     {:x 2 :y 3 :hit? false :ship? false}
                                                     {:x 2 :y 4 :hit? false :ship? false}
                                                     {:x 2 :y 5 :hit? false :ship? false}
                                                     {:x 2 :y 6 :hit? false :ship? false}
                                                     {:x 2 :y 7 :hit? false :ship? false}
                                                     {:x 2 :y 8 :hit? false :ship? false}
                                                     {:x 2 :y 9 :hit? false :ship? false}

                                                     {:x 3 :y 0 :hit? false :ship? true}
                                                     {:x 3 :y 1 :hit? false :ship? false}
                                                     {:x 3 :y 2 :hit? false :ship? false}
                                                     {:x 3 :y 3 :hit? false :ship? false}
                                                     {:x 3 :y 4 :hit? false :ship? false}
                                                     {:x 3 :y 5 :hit? false :ship? false}
                                                     {:x 3 :y 6 :hit? false :ship? false}
                                                     {:x 3 :y 7 :hit? false :ship? false}
                                                     {:x 3 :y 8 :hit? false :ship? false}
                                                     {:x 3 :y 9 :hit? false :ship? false}

                                                     {:x 4 :y 0 :hit? false :ship? true}
                                                     {:x 4 :y 1 :hit? false :ship? false}
                                                     {:x 4 :y 2 :hit? false :ship? false}
                                                     {:x 4 :y 3 :hit? false :ship? false}
                                                     {:x 4 :y 4 :hit? false :ship? false}
                                                     {:x 4 :y 5 :hit? false :ship? false}
                                                     {:x 4 :y 6 :hit? false :ship? false}
                                                     {:x 4 :y 7 :hit? false :ship? false}
                                                     {:x 4 :y 8 :hit? false :ship? false}
                                                     {:x 4 :y 9 :hit? false :ship? false}

                                                     {:x 5 :y 0 :hit? false :ship? true}
                                                     {:x 5 :y 1 :hit? false :ship? false}
                                                     {:x 5 :y 2 :hit? false :ship? false}
                                                     {:x 5 :y 3 :hit? false :ship? false}
                                                     {:x 5 :y 4 :hit? false :ship? false}
                                                     {:x 5 :y 5 :hit? false :ship? false}
                                                     {:x 5 :y 6 :hit? false :ship? false}
                                                     {:x 5 :y 7 :hit? false :ship? false}
                                                     {:x 5 :y 8 :hit? false :ship? false}
                                                     {:x 5 :y 9 :hit? false :ship? false}

                                                     {:x 6 :y 0 :hit? false :ship? false}
                                                     {:x 6 :y 1 :hit? false :ship? false}
                                                     {:x 6 :y 2 :hit? false :ship? false}
                                                     {:x 6 :y 3 :hit? false :ship? false}
                                                     {:x 6 :y 4 :hit? false :ship? false}
                                                     {:x 6 :y 5 :hit? false :ship? false}
                                                     {:x 6 :y 6 :hit? false :ship? false}
                                                     {:x 6 :y 7 :hit? false :ship? false}
                                                     {:x 6 :y 8 :hit? false :ship? false}
                                                     {:x 6 :y 9 :hit? false :ship? false}

                                                     {:x 7 :y 0 :hit? false :ship? false}
                                                     {:x 7 :y 1 :hit? false :ship? false}
                                                     {:x 7 :y 2 :hit? false :ship? false}
                                                     {:x 7 :y 3 :hit? false :ship? false}
                                                     {:x 7 :y 4 :hit? false :ship? false}
                                                     {:x 7 :y 5 :hit? false :ship? false}
                                                     {:x 7 :y 6 :hit? false :ship? false}
                                                     {:x 7 :y 7 :hit? false :ship? false}
                                                     {:x 7 :y 8 :hit? false :ship? false}
                                                     {:x 7 :y 9 :hit? false :ship? false}

                                                     {:x 8 :y 0 :hit? false :ship? false}
                                                     {:x 8 :y 1 :hit? false :ship? false}
                                                     {:x 8 :y 2 :hit? false :ship? false}
                                                     {:x 8 :y 3 :hit? false :ship? false}
                                                     {:x 8 :y 4 :hit? false :ship? false}
                                                     {:x 8 :y 5 :hit? false :ship? false}
                                                     {:x 8 :y 6 :hit? false :ship? false}
                                                     {:x 8 :y 7 :hit? false :ship? false}
                                                     {:x 8 :y 8 :hit? false :ship? false}
                                                     {:x 8 :y 9 :hit? false :ship? false}

                                                     {:x 9 :y 0 :hit? false :ship? false}
                                                     {:x 9 :y 1 :hit? false :ship? false}
                                                     {:x 9 :y 2 :hit? false :ship? false}
                                                     {:x 9 :y 3 :hit? false :ship? false}
                                                     {:x 9 :y 4 :hit? false :ship? false}
                                                     {:x 9 :y 5 :hit? false :ship? false}
                                                     {:x 9 :y 6 :hit? false :ship? false}
                                                     {:x 9 :y 7 :hit? false :ship? false}
                                                     {:x 9 :y 8 :hit? false :ship? false}
                                                     {:x 9 :y 9 :hit? false :ship? false}]}
                                  :player-2 {:setup-ships-left ships
                                             :board [{:x 0 :y 0 :hit? false :ship? false}
                                                     {:x 0 :y 1 :hit? false :ship? false}
                                                     {:x 0 :y 2 :hit? false :ship? false}
                                                     {:x 0 :y 3 :hit? false :ship? false}
                                                     {:x 0 :y 4 :hit? false :ship? false}
                                                     {:x 0 :y 5 :hit? false :ship? false}
                                                     {:x 0 :y 6 :hit? false :ship? false}
                                                     {:x 0 :y 7 :hit? false :ship? false}
                                                     {:x 0 :y 8 :hit? false :ship? false}
                                                     {:x 0 :y 9 :hit? false :ship? false}

                                                     {:x 1 :y 0 :hit? false :ship? false}
                                                     {:x 1 :y 1 :hit? false :ship? true}
                                                     {:x 1 :y 2 :hit? false :ship? false}
                                                     {:x 1 :y 3 :hit? false :ship? false}
                                                     {:x 1 :y 4 :hit? false :ship? false}
                                                     {:x 1 :y 5 :hit? false :ship? false}
                                                     {:x 1 :y 6 :hit? false :ship? false}
                                                     {:x 1 :y 7 :hit? false :ship? false}
                                                     {:x 1 :y 8 :hit? false :ship? false}
                                                     {:x 1 :y 9 :hit? false :ship? false}

                                                     {:x 2 :y 0 :hit? false :ship? false}
                                                     {:x 2 :y 1 :hit? false :ship? false}
                                                     {:x 2 :y 2 :hit? false :ship? false}
                                                     {:x 2 :y 3 :hit? false :ship? true}
                                                     {:x 2 :y 4 :hit? false :ship? false}
                                                     {:x 2 :y 5 :hit? false :ship? false}
                                                     {:x 2 :y 6 :hit? false :ship? false}
                                                     {:x 2 :y 7 :hit? false :ship? false}
                                                     {:x 2 :y 8 :hit? false :ship? false}
                                                     {:x 2 :y 9 :hit? false :ship? false}

                                                     {:x 3 :y 0 :hit? false :ship? false}
                                                     {:x 3 :y 1 :hit? false :ship? false}
                                                     {:x 3 :y 2 :hit? false :ship? false}
                                                     {:x 3 :y 3 :hit? false :ship? false}
                                                     {:x 3 :y 4 :hit? false :ship? false}
                                                     {:x 3 :y 5 :hit? false :ship? false}
                                                     {:x 3 :y 6 :hit? false :ship? false}
                                                     {:x 3 :y 7 :hit? false :ship? false}
                                                     {:x 3 :y 8 :hit? false :ship? false}
                                                     {:x 3 :y 9 :hit? false :ship? false}

                                                     {:x 4 :y 0 :hit? false :ship? false}
                                                     {:x 4 :y 1 :hit? false :ship? false}
                                                     {:x 4 :y 2 :hit? false :ship? false}
                                                     {:x 4 :y 3 :hit? false :ship? false}
                                                     {:x 4 :y 4 :hit? false :ship? false}
                                                     {:x 4 :y 5 :hit? false :ship? false}
                                                     {:x 4 :y 6 :hit? false :ship? false}
                                                     {:x 4 :y 7 :hit? false :ship? false}
                                                     {:x 4 :y 8 :hit? false :ship? false}
                                                     {:x 4 :y 9 :hit? false :ship? false}

                                                     {:x 5 :y 0 :hit? false :ship? false}
                                                     {:x 5 :y 1 :hit? false :ship? false}
                                                     {:x 5 :y 2 :hit? false :ship? false}
                                                     {:x 5 :y 3 :hit? false :ship? false}
                                                     {:x 5 :y 4 :hit? false :ship? false}
                                                     {:x 5 :y 5 :hit? false :ship? false}
                                                     {:x 5 :y 6 :hit? false :ship? false}
                                                     {:x 5 :y 7 :hit? false :ship? false}
                                                     {:x 5 :y 8 :hit? false :ship? false}
                                                     {:x 5 :y 9 :hit? false :ship? true}

                                                     {:x 6 :y 0 :hit? false :ship? false}
                                                     {:x 6 :y 1 :hit? false :ship? false}
                                                     {:x 6 :y 2 :hit? false :ship? false}
                                                     {:x 6 :y 3 :hit? false :ship? false}
                                                     {:x 6 :y 4 :hit? false :ship? false}
                                                     {:x 6 :y 5 :hit? false :ship? false}
                                                     {:x 6 :y 6 :hit? false :ship? false}
                                                     {:x 6 :y 7 :hit? false :ship? false}
                                                     {:x 6 :y 8 :hit? false :ship? false}
                                                     {:x 6 :y 9 :hit? false :ship? true}

                                                     {:x 7 :y 0 :hit? false :ship? false}
                                                     {:x 7 :y 1 :hit? false :ship? false}
                                                     {:x 7 :y 2 :hit? false :ship? false}
                                                     {:x 7 :y 3 :hit? false :ship? false}
                                                     {:x 7 :y 4 :hit? false :ship? false}
                                                     {:x 7 :y 5 :hit? false :ship? false}
                                                     {:x 7 :y 6 :hit? false :ship? false}
                                                     {:x 7 :y 7 :hit? false :ship? false}
                                                     {:x 7 :y 8 :hit? false :ship? false}
                                                     {:x 7 :y 9 :hit? false :ship? true}

                                                     {:x 8 :y 0 :hit? false :ship? false}
                                                     {:x 8 :y 1 :hit? false :ship? false}
                                                     {:x 8 :y 2 :hit? false :ship? false}
                                                     {:x 8 :y 3 :hit? false :ship? false}
                                                     {:x 8 :y 4 :hit? false :ship? false}
                                                     {:x 8 :y 5 :hit? false :ship? false}
                                                     {:x 8 :y 6 :hit? false :ship? false}
                                                     {:x 8 :y 7 :hit? false :ship? false}
                                                     {:x 8 :y 8 :hit? false :ship? false}
                                                     {:x 8 :y 9 :hit? false :ship? true}

                                                     {:x 9 :y 0 :hit? false :ship? false}
                                                     {:x 9 :y 1 :hit? false :ship? false}
                                                     {:x 9 :y 2 :hit? false :ship? false}
                                                     {:x 9 :y 3 :hit? false :ship? false}
                                                     {:x 9 :y 4 :hit? false :ship? false}
                                                     {:x 9 :y 5 :hit? false :ship? false}
                                                     {:x 9 :y 6 :hit? false :ship? false}
                                                     {:x 9 :y 7 :hit? false :ship? false}
                                                     {:x 9 :y 8 :hit? false :ship? false}
                                                     {:x 9 :y 9 :hit? false :ship? false}]}}}))

(defn main [] window)
