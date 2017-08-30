(ns fetch-nist-asd.fetch
  "Fetch data from the NIST Atomic Spectra Database."
  (:require [org.httpkit.client :as http]
            [clojure.string :refer [split]]
            [clojure.java.io :as io]))

(def ^:dynamic *ions-url*
  "URL of available ion data API."
  "http://physics.nist.gov/cgi-bin/ASD/lines_pt.pl")

(def ^:dynamic *lines-url*
  "URL of the line API."
  "http://physics.nist.gov/cgi-bin/ASD/lines1.pl")

(defn index
  "Retrieve a list of available ions. Each ion is represented
  by a map with keys `element` and `numeral`"
  []
  (->> @(http/get *ions-url*)
       :body
       (re-seq #"id=\"([A-Z][a-z]*)_([IVXLDCM]+)\"")
       (map (let [seen (atom #{})
                  index (atom 0)]
              (fn [[_ element numeral]]
                {:Z (if (contains? @seen element)
                      @index
                      (do
                        (swap! index inc)
                        (swap! seen conj element)
                        @index))
                 :element element
                 :numeral numeral})))))

(def ^:dynamic *parameters*
  "Parameter map for the line data request."
  {"unit"           "1"    ;; wavelenghts in nm
   "bibrefs"        "0"    ;; references
   "show_obs_wl"    "1"    ;; observed wavelenghts
   "show_calc_wl"   "0"    ;; theoretical wavelenghts
   "A_out"          "0"    ;; ?
   "format"         "1"    ;; ascii format
   "remove_js"      "on"   ;; ?
   "output"         "0"    ;; ?
   "page_size"      "15"   ;; 0 all at once, 1 paginate
   "line_out"       "1"    ;; 0 return all lines, 1 only w/trans probs, 2 only w/egy levl, 3 only w/obs wls
   "order_out"      "0"    ;; output ordering: 0 wavelength, 1 multiplet
   "show_av"        "2"    ;; show wl in Vacuum (<2000A) Air (2000-20000A) Vacuum (>20,000A)
   "max_low_enrg"   ""     ;; maximum lower level energy
   "max_upp_enrg"   ""     ;; maximum upper level energy
   "min_str"        ""     ;; minimum transition strength
   "max_str"        ""     ;; maximum transition strength
   "min_accur"      ""     ;; minimum line accuracy, eg AAA AA A B C
   "min_intens"     ""     ;; minimum relative intensity to return
   "intens_out"     "on"   ;; show relative intensity
   "allowed_out"    "1"    ;; show allowed transitions
   "forbid_out"     "1"    ;; show forbidden transitions
   "conf_out"       "off"   ;; show electron configuration
   "term_out"       "off"   ;; show terms
   "enrg_out"       "off"   ;; show transition energies
   "J_out"          "off"   ;; show J
   "g_out"          "off"   ;; ?
   "spectra"        "H I"})

(defn ascii
  "ASCII Table of an ion."
  ([ion] (ascii ion "I"))
  ([ion numeral]
   (-> *lines-url*
       (http/get {:query-params (assoc *parameters* "spectra" (str ion " " numeral))})
       deref
       :body
       (split #"<pre>|</pre>")
       second)))

(defn write-ascii
  ""
  [{:keys [Z element numeral] :as ion}]
  (let [path (format "/home/tim/src/fetch-nist-asd/resources/ascii-tables-relint/%03d-%s-%s.txt"
                     Z element numeral)]
    (with-open [w (io/writer path)]
      (println Z element numeral)
      (.write w (or (ascii element numeral) "")))))
