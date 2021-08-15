(ns seasons.draw
  (:require [quil.core :as q]))

(defn dotted-line [x1 y1 x2 y2 dots]
  (doseq [i (range dots)]
    (let [x (q/lerp x1 x2 (/ i dots))
          y (q/lerp y1 y2 (/ i dots))]
      (q/point x y))))
