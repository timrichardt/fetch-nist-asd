(ns fetch-nist-asd.nucleosynthesis
  (:require [clojure.java.io]
            [clj-xpath.core :refer [$x:text*]]))

(defn origin-text->origin
  ""
  [origin-text]
  (as-> origin-text _
    (clojure.string/split _ #"\n")
    (map (fn [origin]
           (let [[_ n x] (re-matches #"(.+)% (.+)" origin)]
             [(-> x
                  (clojure.string/trim)
                  (clojure.string/replace "," ""))
              (Float/parseFloat n)])) _)
    (into (sorted-map) _)))

(def Z->origin
  ""
  (let [svg-file (clojure.java.io/resource "Nucleosynthesis periodic table.svg")
        xml (slurp svg-file)
        element-texts ($x:text* "//text" xml)
        [_ & origin-texts] ($x:text* "//title" xml)]
    (into (sorted-map)
          (map (fn [element-text origin-text]
                 (let [[_ _ Z] (re-matches #"([A-Za-z]+)([0-9]+)" element-text)
                       origins (origin-text->origin origin-text)]
                   [(Integer/parseInt Z) origins]))
               element-texts
               origin-texts))))
