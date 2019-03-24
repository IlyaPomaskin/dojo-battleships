(ns dojo-battleships.core
  (:require [clojure2d.core :refer :all])
  (:gen-class))

(def world-size 10)
(def field-size 30)
(def ships [5 4 4 3 3 2 2 2 1 1 1 1])
(def canvas-width  800)
(def canvas-height 600)

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

(defn draw
  [canvas
   window
   ^long framecount
   state]
  (set-background canvas :lightblue 200)
  (render-board canvas (get-in state [(:current-player state) :board]))
  state)

;; create canvas, display window and draw on canvas via draw function
;; show-window {:keys [canvas window-name w h fps draw-fn state draw-state setup hint refresher always-on-top? background]
(def window (show-window {:canvas (canvas canvas-width canvas-height)
                          :window-name "Battleships"
                          :fps 25
                          :draw-fn draw
                          :setup (fn [canvas window]
                                   {:state :setup ; :game :game-over
                                    :current-player :player-1

                                    :player-1 {:setup-ships-left ships
                                               :board [{:x 0 :y 0 :hit? false :ship? false}
                                                       {:x 1 :y 0 :hit? false :ship? true}
                                                       {:x 0 :y 1 :hit? true :ship? false}
                                                       {:x 1 :y 1 :hit? true :ship? true}]}
                                    :player-2 {:setup-ships-left ships
                                               :board [{:x 0 :y 0 :hit? false :ship? false}
                                                       {:x 1 :y 0 :hit? false :ship? false}
                                                       {:x 4 :y 0 :hit? false :ship? false}]}})}))

(defn main []
  window)

