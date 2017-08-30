(ns fetch-nist-asd.parse)

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
  (->> (replace line " " "")
       (re-find #"^[0-9]+\.?[0-9]*")))

(defn observed-lines
  "Given a NIST ASD ASCII table, returns observed lines in
  nm."
  [ascii]
  (->> (split ascii #"\n")
       (map observed-line)
       (filter identity)
       Double/parseDouble))

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
  (->> (split ascii #"\n")
       (map relint-line)
       (filter identity)))

(comment
  (doseq [{:keys [Z element numeral]} (index)]
    (let [home "/home/tim/src/nist-asd-clj/resources"
          ascii-path (format "%s/ascii-tables-relint/%03d-%s-%s.txt" home Z element numeral)
          lines (-> ascii-path
                    slurp
                    observed-lines-with-relative-intensity)
          csv-path (format "%s/lines-relint/%03d-%s-%s.csv" home Z element numeral)]
      (->> lines
           (map (fn [[l i]]
                  (str l "," i)))
           (interpose "\n")
           (apply str)
           (write csv-path)))))
