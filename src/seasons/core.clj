(ns seasons.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [seasons.draw :as draw]
            [seasons.model :as model]))

(def colors {:sun [246 255 17]})

(def canvas-size 800)

(defn setup []
  (q/frame-rate 30)
  {:sun {:pos [50 250]
         :size 50}
   :earth {:pos [350 250]
           :size 100
           :angle 23.4}})

(defn summer [state]
  (assoc-in state [:earth :angle] 23.4))

(defn winter [state]
  (assoc-in state [:earth :angle] (- 360 23.4)))

(defn sin-tilt []
  (let [d 23.4]
    (* (Math/sin (/ (q/millis) 1000))
       d)))

(defn update-state [state]
  ;;(assoc-in state [:earth :angle] (sin-tilt))
  state)

(defn draw-circle [x y size]
  (q/ellipse x y size size))

(defn draw-sun [{:keys [sun] :as state}]
  (q/stroke 0 0 0)
  (apply q/fill (:sun colors))
  (apply draw-circle (into (:pos sun) [(:size sun)])))

(defn earth-surface-point [{:keys [earth] :as state} degrees]
  (let [{:keys [angle pos size]} earth
        scale (/ size 2)
        [x y] pos]
    [(+ x (*    scale (Math/cos (Math/toRadians degrees))))
     (+ y (* -1 scale (Math/sin (Math/toRadians degrees))))]))

(defn draw-equator [{:keys [earth] :as state}]
  (q/stroke-weight 0.4)
  (q/stroke 0 0 0)
  (let [angle (:angle earth)
        begin angle
        end (+ begin 180)
        num-points 40
        [x y] (earth-surface-point state begin)
        [a b] (earth-surface-point state end)]
    (q/line x y a b)))

(defn draw-tropics [{:keys [earth] :as state}]
  (q/stroke-weight 0.2)
  (q/stroke 17 39 255)
  (let [tropic-points 8
        angle (:angle earth)
        d model/tropical-offset]
    (let [[x y] (earth-surface-point state (+ angle d))
          [a b] (earth-surface-point state (- (+ 180 angle) d))]
      (q/line x y a b))
    (let [[x y] (earth-surface-point state (+ 180 d angle))
          [a b] (earth-surface-point state (- (+ 360 angle) d))]
      (q/line x y a b))))

(defn draw-polar-axis [{:keys [earth] :as state}]
  (q/stroke 150 0 0)
  (q/stroke-weight 0.5)
  (let [begin (+ 90 (:angle earth))
        end (+ begin 180)
        [x y] (earth-surface-point state begin)
        [a b] (earth-surface-point state end)]
    (q/line x y a b)))

(defn draw-solar-line [{:keys [earth sun] :as state}]
  (q/stroke-weight 2)
  (q/stroke 0 0 0)
  (let [[x y] (:pos earth)
        [a b] (:pos sun)
        solar-points 20]
    (draw/dotted-line x y a b solar-points)))

(defn draw-earth [{:keys [earth] :as state}]
  (q/stroke 100 100 100)
  (q/fill 67 176 110)
  (apply draw-circle (into (:pos earth) [(:size earth)]))
  (draw-tropics state)
  (draw-equator state)
  (draw-polar-axis state))

(defn draw-earth-scenario [state]
  (q/stroke-weight 2)
  (q/stroke 100 100 100)

  (draw-solar-line state)
  (draw-earth state)
  (draw-sun state))

(defn draw-state [state]
  (q/background 240)
  (let [spring {:sun {:pos [350 100]
                      :size 50}
                :earth {:pos [350 100]
                        :size 100
                        :angle 0}}
        summer {:sun {:pos [50 300]
                      :size 50}
                :earth {:pos [350 300]
                        :size 100
                        :angle 23.4}}
        fall {:sun {:pos [350 500]
                    :size 0}
              :earth {:pos [350 500]
                      :size 100
                      :angle 0}}
        winter {:sun {:pos [650 700]
                      :size 50}
                :earth {:pos [350 700]
                        :size 100
                        :angle (- 360 23.4)}}]
    (draw-earth-scenario spring)
    (draw-earth-scenario summer)
    (draw-earth-scenario fall)
    (draw-earth-scenario winter)))

#_(q/defsketch seasons
  :title "The Seasons"
  :size [canvas-size canvas-size]
  :setup setup
  :update update-state
  :draw draw-state
  :features [:keep-on-top]
  :middleware [m/fun-mode])
