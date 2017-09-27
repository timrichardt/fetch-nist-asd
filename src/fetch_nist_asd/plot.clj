(ns fetch-nist-asd.plot
  (:require [clojure.string]
            [hiccup.core :refer [html]]
            [hiccup.page :refer [html5]]))

(def data
  "A sequence of `java.io.File` objects to the line data."
  (->> "/home/tim/science/planck/nist-asd-data/lines/"
       clojure.java.io/file
       file-seq
       (filter (comp (partial re-matches #".*-I.txt") #(.getPath %)))
       (sort-by #(.getPath %))))

(defn file->data
  "returns a datastructure of the form
  {:Z 1
   :element 'H'
   :data ['91.8125' '91.81293175' ... ]}"
  [file]
  (let [[_ Z element] (re-matches #"([0-9]+)-([A-Za-z]+).*" (.getName file))
        Z (Integer/parseInt Z)]
    {:element element
     :Z Z
     :data (->> (clojure.string/split (slurp file ) #",")
                (map #(Double/parseDouble %)))}))

(defn bincount
  "Returns the number of points for each bin."
  [minimum bin-size number-of-bins {:keys [data] :as file}]
  (assoc file :bincount
         (for [bin (map #(+ minimum (* bin-size %)) (range number-of-bins))]
           (count (filter #(and (<= bin %) (< % (+ bin bin-size))) data)))))

;; 320nm rect (gelber Hintergrund)
#_[:rect {:x 0 , :y 0, :width 22, :height (- height 4)
          :stroke-width 0, :fill "#fec"}]

(defn histogram
  ""
  ([file] (histogram file {:width 480
                           :height 400
                           :wrap :svg}))
  ([{:keys [Z element bincount] :as file}
    {:keys [width height wrap] :as options}]
   (let [maximum (apply max bincount)
         number-of-bins (count bincount)]
     (html
      (into (cond (= wrap :svg)
                  [:svg {:version "1.1"
                         :base-profile "full"
                         :xmlns "http://www.w3.org/2000/svg"
                         :width width, :height height}]

                  :otherwise
                  [:g])
            [[:rect {:x 1, :y 1, :width (- width 2), :height (- height 2)
                     :stroke-width 2, :stroke "black", :fill "none"}]
             (for [[bin index] (map vector bincount (range))]
               (let [bar-height (* height (/ bin maximum))]
                 [:rect {:y (- height bar-height), :x index
                         :height bar-height, :width 1
                         :fill "black", :stroke-width 0}]))
             [:text {:x (* 2 (/ width 5)), :y (/ height 5)
                     :style {:font-size "3em", :fill "black"}}
              (str Z " " element)]
             [:text {:x (* 2 (/ width 5)), :y (* 2 (/ height 5))
                     :style {:font-size "3em", :fill "black"}}
              (str (reduce + bincount) " lines")]])))))

(defn element-matrix
  ""
  [files]
  (html5
   [:body
    [:div
     (for [row (partition 2 files)]
       [:div
        (for [file row]
          (->> file
               file->data
               (bincount 0 5 240)
               histogram))])]]))

(defn atom-histograms
  ""
  [files]
  (doseq [file files]
    (let [{:keys [Z element] :as data} (file->data file)
          output-path (format "/home/tim/science/planck/histograms/I/%03d-%s-I.svg"
                              Z element)]
      (->> data
           (bincount 0 5 240)
           histogram
           (spit output-path)))))

(def periodic-system-pattern
  ""
  (let [pattern-1  [[0 17]
                    (concat [0 1] (range 12 18))
                    (concat [0 1] (range 12 18))
                    (range 18)
                    (range 18)
                    [0 1 2]
                    []
                    []
                    (range 3 17)]
        pattern-2  [[]
                    []
                    []
                    []
                    []
                    (range 3 18)
                    [0 1 2]
                    []
                    []
                    (range 3 17)]]
    (concat
     (->> pattern-1
          (mapcat (fn [y-offset row]
                    (map (fn [column]
                           {:x-offset column
                            :y-offset y-offset})
                         row))
                  (range 0 70))
          (map #(assoc %2 :Z %1) (range 1 72)))
     (->> pattern-2
          (mapcat (fn [y-offset row]
                    (map (fn [column]
                           {:x-offset column
                            :y-offset y-offset})
                         row))
                  (range))
          (map #(assoc %2 :Z %1) (range 72 100))))))

(defn periodic-system-histograms
  [files]
  (spit "/home/tim/test.svg"
        (html
         [:svg {:version "1.1"
                :base-profile "full"
                :xmlns "http://www.w3.org/2000/svg"
                :width 15000, :height 10000}
          (for [{:keys [x-offset y-offset Z]} periodic-system-pattern]
            (let [index (dec Z)
                  spectrum (->> (nth files index)
                                file->data
                                (bincount 0 5 240))]
              [:g {:transform (str "translate(" (* x-offset 480) " " (* y-offset 400) ")")}
               (histogram spectrum {:width 480, :height 400, :wrap :g})]))])))

(periodic-system-histograms data)
