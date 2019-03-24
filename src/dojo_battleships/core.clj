(ns dojo-battleships.core
    (:require [clojure2d.core :refer :all])
    (:gen-class))

(def canvas-width  800)
(def canvas-height 600)

(defn draw
  "Some function decription."
  [canvas ;; canvas to draw on
   window ;; window bound to function (for mouse movements)
   ^long framecount ;; frame number
   state] ;; state (if any)
  () ; should return a new state
  )

;; create canvas, display window and draw on canvas via draw function (60 fps)
;; show-window {:keys [canvas window-name w h fps draw-fn state draw-state setup hint refresher always-on-top? background]
(def window (show-window {:canvas (canvas canvas-width canvas-height),
                          :window-name "Battleships",
                          :fps 25,
                          :draw-fn draw,
                          :setup (fn [canvas window] ())}) ; returns initial state
  )

(defn main []
  window)

