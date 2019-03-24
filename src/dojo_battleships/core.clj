(ns dojo-battleships.core
  (:require [clojure2d.core :refer :all])
  (:gen-class))

(def field-size 10)
(def ships [5 4 4 3 3 2 2 2 1 1 1 1])
(def canvas-width  800)
(def canvas-height 600)


(defn render-board [board] nil)

(defn handle-click [event state] state)

(defn draw
  "Some function decription."
  [canvas ;; canvas to draw on
   window ;; window bound to function (for mouse movements)
   ^long framecount ;; frame number
   state] ;; state (if any)
  nil ; should return a new state
  )

;; create canvas, display window and draw on canvas via draw function (60 fps)
;; show-window {:keys [canvas window-name w h fps draw-fn state draw-state setup hint refresher always-on-top? background]
(def window (show-window {:canvas (canvas canvas-width canvas-height)
                          :window-name "Battleships"
                          :fps 25
                          :draw-fn draw
                          :setup (fn [canvas window]
                                   {:state :setup ; :game :game-over
                                    :setup-ships-left ships
                                    :current-player :player-1
                                    :board [{:x 0 :y 0 :hit? false :ship? false}]})}))

(defn main []
  window)

