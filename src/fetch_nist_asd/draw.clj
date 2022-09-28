(ns fetch-nist-asd.draw
  (:require [clojure.java.io]
            [hiccup.core :as hiccup]
            [fetch-nist-asd.parse :as parse]))

(def PLOT-WIDTH 500)
(def PLOT-HEIGHT 250)

(def init-points
  {:points
   {"2022-09-09T20:26:55.376Z"
    {:id "2022-09-09T20:26:55.376Z", :curve :red, :x 235, :y 104},
    "2022-09-09T23:29:03.154Z"
    {:id "2022-09-09T23:29:03.154Z", :curve :red, :x 483, :y 95},
    "2022-09-17T12:22:27.011Z"
    {:id "2022-09-17T12:22:27.011Z", :curve :green, :x 258, :y 46},
    "2022-09-09T20:24:11.381Z"
    {:id "2022-09-09T20:24:11.381Z", :curve :red, :x 87, :y 51},
    "2022-09-09T20:23:46.409Z"
    {:id "2022-09-09T20:23:46.409Z", :curve :red, :x 374, :y 168},
    "2022-09-09T23:37:38.737Z"
    {:id "2022-09-09T23:37:38.737Z", :curve :red, :x 162, :y 37},
    :green/left {:id :green/left, :curve :green, :x 0, :y 144},
    "2022-09-09T20:26:51.915Z"
    {:id "2022-09-09T20:26:51.915Z", :curve :blue, :x 261, :y 134},
    "2022-09-09T23:20:06.062Z"
    {:id "2022-09-09T23:20:06.062Z", :curve :blue, :x 395, :y 18},
    :red/right {:id :red/right, :curve :red, :x 500, :y 86},
    "2022-09-09T23:37:33.883Z"
    {:id "2022-09-09T23:37:33.883Z", :curve :green, :x 156, :y 65},
    "2022-09-09T23:20:05.299Z"
    {:id "2022-09-09T23:20:05.299Z", :curve :blue, :x 453, :y 53},
    "2022-09-09T20:24:18.283Z"
    {:id "2022-09-09T20:24:18.283Z", :curve :red, :x 43, :y 64},
    "2022-09-09T23:28:57.260Z"
    {:id "2022-09-09T23:28:57.260Z", :curve :red, :x 466, :y 112},
    "2022-09-09T22:58:48.091Z"
    {:id "2022-09-09T22:58:48.091Z", :curve :blue, :x 128, :y 150},
    "2022-09-09T20:26:59.017Z"
    {:id "2022-09-09T20:26:59.017Z", :curve :green, :x 226, :y 47},
    "2022-09-09T23:19:59.933Z"
    {:id "2022-09-09T23:19:59.933Z", :curve :blue, :x 319, :y 71},
    "2022-09-09T20:23:41.749Z"
    {:id "2022-09-09T20:23:41.749Z", :curve :red, :x 178, :y 29},
    "2022-09-09T23:00:36.003Z"
    {:id "2022-09-09T23:00:36.003Z", :curve :green, :x 371, :y 140},
    "2022-09-09T20:24:42.104Z"
    {:id "2022-09-09T20:24:42.104Z", :curve :blue, :x 363, :y 20},
    :blue/right {:id :blue/right, :curve :blue, :x 500, :y 59},
    "2022-09-09T23:28:09.181Z"
    {:id "2022-09-09T23:28:09.181Z", :curve :green, :x 182, :y 29},
    :red/left {:id :red/left, :curve :red, :x 0, :y 82},
    "2022-09-09T20:24:20.654Z"
    {:id "2022-09-09T20:24:20.654Z", :curve :red, :x 12, :y 76},
    "2022-09-09T20:24:42.864Z"
    {:id "2022-09-09T20:24:42.864Z", :curve :blue, :x 339, :y 46},
    "2022-09-09T20:24:05.430Z"
    {:id "2022-09-09T20:24:05.430Z", :curve :blue, :x 295, :y 97},
    "2022-09-09T20:23:57.295Z"
    {:id "2022-09-09T20:23:57.295Z", :curve :green, :x 288, :y 60},
    "2022-09-09T20:24:25.957Z"
    {:id "2022-09-09T20:24:25.957Z", :curve :green, :x 92, :y 91},
    "2022-09-09T20:23:48.213Z"
    {:id "2022-09-09T20:23:48.213Z", :curve :red, :x 274, :y 131},
    :blue/left {:id :blue/left, :curve :blue, :x 0, :y 147},
    "2022-09-09T23:28:03.153Z"
    {:id "2022-09-09T23:28:03.153Z", :curve :green, :x 139, :y 75},
    "2022-09-09T23:29:04.457Z"
    {:id "2022-09-09T23:29:04.457Z", :curve :red, :x 460, :y 122},
    "2022-09-09T20:24:44.192Z"
    {:id "2022-09-09T20:24:44.192Z", :curve :blue, :x 441, :y 36},
    "2022-09-17T22:42:29.630Z"
    {:id "2022-09-17T22:42:29.630Z", :curve :red, :x 452, :y 135},
    "2022-09-09T23:00:47.108Z"
    {:id "2022-09-09T23:00:47.108Z", :curve :red, :x 210, :y 79},
    :green/right {:id :green/right, :curve :green, :x 500, :y 96},
    "2022-09-09T22:58:19.651Z"
    {:id "2022-09-09T22:58:19.651Z", :curve :red, :x 141, :y 42}},
   :curves
   {:red
    {:name "red",
     :points
     #{"2022-09-09T20:26:55.376Z" "2022-09-09T23:29:03.154Z"
       "2022-09-09T20:24:11.381Z"
       "2022-09-09T20:23:46.409Z"
       "2022-09-09T23:37:38.737Z"
       :red/right
       "2022-09-09T20:24:18.283Z"
       "2022-09-09T23:28:57.260Z"
       "2022-09-09T20:23:41.749Z"
       :red/left
       "2022-09-09T20:24:20.654Z"
       "2022-09-09T20:23:48.213Z"
       "2022-09-09T23:29:04.457Z"
       "2022-09-17T22:42:29.630Z"
       "2022-09-09T23:00:47.108Z"
       "2022-09-09T22:58:19.651Z"}},
    :green
    {:name "green",
     :points
     #{"2022-09-17T12:22:27.011Z" :green/left "2022-09-09T23:37:33.883Z"
       "2022-09-09T20:26:59.017Z"
       "2022-09-09T23:00:36.003Z"
       "2022-09-09T23:28:09.181Z"
       "2022-09-09T20:23:57.295Z"
       "2022-09-09T20:24:25.957Z"
       "2022-09-09T23:28:03.153Z"
       :green/right}},
    :blue
    {:name "blue"
     :points
     #{"2022-09-09T20:26:51.915Z" "2022-09-09T23:20:06.062Z"
       "2022-09-09T23:20:05.299Z"
       "2022-09-09T22:58:48.091Z"
       "2022-09-09T23:19:59.933Z"
       "2022-09-09T20:24:42.104Z"
       :blue/right
       "2022-09-09T20:24:42.864Z"
       "2022-09-09T20:24:05.430Z"
       :blue/left "2022-09-09T20:24:44.192Z"}}}})

(defn bin
  ""
  ([lines] (let [bins (range 380 705 5)]
             (bin lines bins 5 [])))
  ([lines bins bin-width res]
   (let [[bin & bins]         bins
         [lines-in-bin lines] (split-with #(<= bin % (+ bin bin-width)) lines)]
     #_(println "bin" bin "lines-in-bin" (count lines-in-bin))
     (if (empty? bins)
       res
       (recur lines bins bin-width (conj res [bin (count lines-in-bin)]))))))


(defn color-fn
  [curve]
  (fn [x]
    (reduce (fn [a b]
              (cond (<= (:x a) x (:x b))
                    (reduced (float (* 255 (- 1 (/ (+ (:y a)
                                                      (* (- x (:x a))
                                                         (/ (- (:y b) (:y a)) (- (:x b) (:x a)))))
                                                   PLOT-HEIGHT)))))

                    :else b))
            (->> (get-in init-points [:curves curve :points])
                 (map (fn [point-id] (get-in init-points [:points point-id])))
                 (sort-by :x)))))


(defn make-pic []
  (hiccup/html
   [:svg {:xmlns    "http://www.w3.org/2000/svg"
          :version  "1.1"
          :width    (+ 100 PLOT-WIDTH)
          :height   (+ 100 300)
          :viewBox  (str "-50 -50 " (+ 100 PLOT-WIDTH) " " (+ 100 300))
          :overflow "visible"}
    #_[:link {:rel "stylesheet" :type "text/css" :href "/main.css"}]
    [:style
     "@keyframes glitch1 {
  0% { opacity: 1; transform: translateX(0px); }
  80% { opacity: 1; transform: translateX(0px); }
  82% { opacity: 0; transform: translateX(-10px); }
  83% { opacity: 1; transform: translateX(0px); }
  90.5% { opacity: 1; transform: translateX(0px); }
  91% { opacity: 0; transform: translateX(30px); }
  91.5% { opacity: 1; transform: translateX(40px); }
  95% { opacity: 0; transform: translateX(0px); }
  100% { opacity: 1; transform: translateX(0px); }
}

@keyframes glitch2 {
  0% { opacity: 1; transform: translateX(0px); }
  20% { opacity: 1; transform: translateX(0px); }
  22% { opacity: 0; transform: translateX(-10px); }
  23% { opacity: 1; transform: translateX(0px); }
  90.5% { opacity: 1; transform: translateX(0px); }
  91% { opacity: 0; transform: translateX(30px); }
  91.5% { opacity: 1; transform: translateX(30px); }
  95% { opacity: 0; transform: translateX(0px); }
  100% { opacity: 1; transform: translateX(0px); }
}

@keyframes wobble {
0% { transform: translateX(0px) }
25% { transform: translateX(1px) }
50% { transform: translateX(0px) }
75% { transform: translate(-2px) }
100% { transform: translate(0px) }
}
"]
    (let [visible-lines (parse/visible-lines)
          rgb-fn        (fn [x] (str "rgb(" ((color-fn :red) x) "," ((color-fn :green) x) "," ((color-fn :blue) x) ")"))]
      (doall
       (let [peak (/ (apply max (mapcat (comp second bin :lines) visible-lines)) 1)]
         (for [[Z lines] (map (juxt :Z (comp bin :lines)) (take 50 visible-lines))]
           ^{:key (str "line-" Z)}
           [:g {:id        (str "line-" Z)
                :class     (str "line-" Z)}
            (for [[wl cnt] lines]
              ^{:key (str "bin-" Z "-" wl)}
              (let [fluminosity (java.lang.Math/pow (/ cnt peak) 0.567)] ;; fake luminosity
                (when (> fluminosity 0.1)
                  [:rect {:style   {;; :animation (str "wobble 0.2s ease infinite")
                                        ;:transform (str "translate(" (- 50 Z) " 0)")
                                    ;; :animation-delay (format "%.2fs" (* (/ Z 50) 0.2))
                                    }
                          :y       (* 3 Z)
                          :x (+ 20 (/ (java.lang.Math/pow (- Z 35) 2) 35) (rand-int 3) (- (* PLOT-WIDTH (/ (- wl 380) 320)) (- (rand-int 4) 2)))
                          :width   (- (/ PLOT-WIDTH (/ 320 5)) (rand-int 2))
                          :height  4.5
                          :rx      2
                          :ry      2
                          :fill    (rgb-fn (* 500 PLOT-WIDTH (/ (- wl 380) 320 PLOT-WIDTH)))
                          :stroke  "rgba(0,0,0,0)"
                          :opacity (str (java.lang.Math/pow (/ cnt peak) 0.567))}])))]))))]))

(with-open [w (clojure.java.io/writer "/home/tim/src/verlag/target/rainbow.svg")]
  (.write w (make-pic)))

                                        #_(with-open [w (clojure.java.io/writer "/home/tim/src/verlag/target/hatch.svg")]
    (.write w 
            (hiccup/html
             [:svg {:xmlns    "http://www.w3.org/2000/svg"
                    :version  "1.1"
                    :width    "5.345059133378628"
                    :height   "5.345059133378628"
                    :viewbox  "0 0 5.345059133378628 5.345059133378628"
                    :overflow "hidden"}
              [:defs
               [:pattern {:id           "Hatch1.0x045"
                          :x            "0"
                          :y            "0"
                          :width        "5.345059133378628"
                          :height       "5.345059133378628"
                          :patternUnits "userSpaceOnUse"}
                [:path {:style {:stroke-width "0.25mm"
                                :stroke       "black"}
                        :d     "M -3.7795275590551185,3.7795275590551185 3.7795275590551185,-3.7795275590551185"}]
                [:path {:style {:stroke-width "0.25mm"
                                :stroke       "black"}
                        :d     "M -3.7795275590551185,9.124586692433747 9.124586692433747,-3.7795275590551185"}]
                [:path {:style {:stroke-width "0.25mm"
                                :stroke       "black"}
                        :d     "M 1.5655315743235096,9.124586692433747 9.124586692433747,1.5655315743235096"}]]]
              [:rect {:x0 0 :y0 0 :width "100%" :height "100%" :fill "url(#Hatch1.0x045)" :opacity "0.125"}]])))
