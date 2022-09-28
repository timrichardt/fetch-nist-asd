(ns fetch-nist-asd.parse
  (:require [clojure.string]
            [clojure.java.io]
            [fetch-nist-asd.fetch :refer [index]]))

(def relint-descriptors
  "Table of the descriptors sometimes added to lines where
  the relative Intensity is known."
  {"*"    "Intensity is shared by several lines (typically, for multiply classified lines)."
   ":"    "Observed value given is actually the rounded Ritz value, e.g., Ar IV, λ = 443.40 Å."
   "-"    "Somewhat lower intensity than the value given."
   "a"    "Observed in absorption."
   "b"    "Band head."
   "bl"   "Blended with another line that may affect the wavelength and intensity."
   "B"    "Line or feature having large width due to autoionization broadening."
   "c"    "Complex line."
   "d"    "Diffuse line."
   "D"    "Double line."
   "E"    "Broad due to overexposure in the quoted reference"
   "f"    "Forbidden line."
   "g"    "Transition involving a level of the ground term."
   "G"    "Line position roughly estimated."
   "H"    "Very hazy line."
   "h"    "Hazy line (same as diffuse)."
   "hfs"  "Line has hyperfine structure."
   "i"    "Identification uncertain."
   "j"    "Wavelength smoothed along isoelectronic sequence."
   "l"    "Shaded to longer wavelengths; NB: This may look like a one at the end of the number!"
   "m"    "Masked by another line (no wavelength measurement)."
   "p"    "Perturbed by a close line. Both wavelength and intensity may be affected."
   "q"    "Asymmetric line."
   "r"    "Easily reversed line."
   "s"    "Shaded to shorter wavelengths."
   "t"    "Tentatively classified line."
   "u"    "Unresolved from a close line."
   "w"    "Wide line."
   "x"    "Extrapolated wavelength"})

(defn- observed-line
  "Observed number string from line (always the first column)"
  [line]
  (->> (clojure.string/replace line " " "")
       (re-find #"^[0-9]+\.?[0-9]*")))

(defn observed-lines
  "Given a NIST ASD ASCII table, returns observed lines in
  nm."
  [ascii]
  (->> (clojure.string/split ascii #"\n")
       (map observed-line)
       (filter identity)
       (map #(Double/parseDouble %))))

(defn- relint-line
  "Return a vector with `[wavelength intensity]`"
  [line]
  (->> (replace line " " "")
       (re-matches #"^(\d+\.?\d*)\|\(?(\d+\.?\d*)\)?.*")
       ((fn [[_ line intensity]] [(Double/parseDouble line)
                                  (Double/parseDouble intensity)]))))

(defn observed-lines-with-relative-intensity
  "Given an ASCII table without the Ritz wavelenghts, returns
  a list of observed lines with relative intensity."
  [ascii]
  (->> (clojure.string/split ascii #"\n")
       (map relint-line)
       (filter identity)))


(defn visible-line?
  [wl]
  (<= 380 wl 700))

(defn neutral?
  [element]
  (= (:numeral element) "I"))

(comment
  ;; Write lines with relative intensity per ion into some directory
  (doseq [{:keys [Z element numeral]} (take 5 (filter #(= (:numeral %) "I") (index)))]
    (let [home "/home/tim/src/spectra"
          ascii-path (format "%s/%03d-%s-%s.txt" home Z element numeral)
          lines (->> ascii-path
                     slurp
                     observed-lines)
          ;; csv-path (format "%s/csv/%03d-%s-%s.csv" home Z element numeral)
          ]
      (pr-str (take 5 lines))
      #_(->> lines
             (map (fn [[l i]]
                    (str l "," i)))
             (interpose "\n")
             (apply str)
             (println)
             #_(spit csv-path))))

  (doseq [{:keys [Z element numeral]} (take 1 (filter #(= (:numeral %) "I") (index)))]
    (let [home "/home/tim/src/spectra"
          ascii-path (format "%s/%03d-%s-%s.txt" home Z element numeral)
          lines (->> ascii-path
                     slurp
                     observed-lines)]
      (pr-str (take 5 lines))))

  ;; Write lines with relative intensity into a clojure data structure
  (with-open [w (clojure.java.io/writer "/home/tim/data.cljdat")]
    (.write w
            (prn-str
             (for [{:keys [Z element numeral] :as ion} (filter neutral? (index))]
               (let [home "/home/tim/src/spectra"
                     ascii-path (format "%s/%03d-%s-%s.txt" home Z element numeral)
                     lines (as-> ascii-path _
                             (slurp _)
                             (observed-lines _)
                             (filter visible-line? _))]
                 (merge ion {:lines lines})))))))


(defn visible-lines
  []
  (for [{:keys [Z element numeral] :as ion} (filter neutral? (index))]
    (let [home "/home/tim/src/spectra"
          ascii-path (format "%s/%03d-%s-%s.txt" home Z element numeral)
          lines (as-> ascii-path _
                  (slurp _)
                  (observed-lines _)
                  (filter visible-line? _))]
      (merge ion {:lines lines}))))
