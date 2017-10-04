(ns fetch-nist-asd.color
  "Colormaps.")

(defn slope
  "The slope between two points."
  [[px py] [qx qy]]
  (/ (- qy py) (- qx px)))

(defn linear-interpolation
  "Returns a function, that is a linear interpolation of
  `points`."
  [& points]
  (let [[minimum-x _] (first points)
        [maximum-x _] (last points)]
    (fn [x]
      (if (or (< x minimum-x) (> x maximum-x))
        0
        (reduce (fn [[left-x  left-y :as left]
                     [right-x _      :as right]]
                  (if (<= x right-x)
                    (reduced (+ left-y (* (slope left right) (- x left-x))))
                    right))
                points)))))

(defmacro defcolormap
  "Creates a colormap function that maps from x to-> [r, g b]."
  [name support]
  `(defn ~name
     [x#]
     (let [[r# g# b#] (map (partial apply linear-interpolation) ~support)]
       [(r# x#) (g# x#) (b# x#)])))

(defn- floor
  [x]
  (Math/floor x))

(defn rgb255
  [rgb]
  (mapv (comp int floor (partial * 255)) rgb))

(defn hex
  [rgb]
  (->> rgb
       rgb255
       (apply (partial format "#%02x%02x%02x"))))

;; --------------------
;; Colormaps

(defcolormap hot
  [[[0 0] [0.02 0.3] [0.3 1] [1 1]]
   [[0 0] [0.3 0]    [0.7 1] [1 1]]
   [[0 0] [0.7 0]    [1 1]]])

(defn hot-inverted
  [x]
  (mapv (partial - 1) (hot x)))
