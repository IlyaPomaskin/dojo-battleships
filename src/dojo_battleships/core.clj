(ns dojo-battleships.core
  (:require [clojure2d.core :refer :all])
  (:gen-class))

(def world-size 10)
(def field-size 30)
(def ships [5 4 4 3 3 2 2 2 1 1 1 1])
(def canvas-width  800)
(def canvas-height 600)

(defn render-board [canvas board]
  (set-color canvas :white 160)
  (mapv #(rect canvas
               (* field-size (:x %1))
               (* field-size (:y %1))
               field-size
               field-size
               true)
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
                                                       {:x 1 :y 0 :hit? false :ship? false}
                                                       {:x 0 :y 1 :hit? false :ship? false}
                                                       {:x 1 :y 1 :hit? false :ship? false}]}
                                    :player-2 {:setup-ships-left ships
                                               :board [{:x 0 :y 0 :hit? false :ship? false}
                                                       {:x 1 :y 0 :hit? false :ship? false}
                                                       {:x 4 :y 0 :hit? false :ship? false}] }
                                    })}))

(defn main []
  window)

