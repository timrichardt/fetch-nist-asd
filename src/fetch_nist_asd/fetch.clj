(ns fetch-nist-asd.fetch
  "Fetch data from the NIST Atomic Spectra Database."
  (:require [org.httpkit.client :as http]
            [clojure.string :refer [split]]
            [clojure.java.io :as io]))

(def ^:dynamic *ions-url*
  "URL of available ion data API."
  "https://physics.nist.gov/cgi-bin/ASD/lines_pt.pl")

(def ^:dynamic *lines-url*
  "URL of the line API."
  "https://physics.nist.gov/cgi-bin/ASD/lines1.pl")

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

;; spectra=H+I
;; limits_type=0
;; low_w=
;; upp_w=
;; unit=1
;; de=0
;; format=1
;; line_out=0
;; remove_js=on
;; en_unit=0
;; output=0
;; bibrefs=1
;; page_size=15
;; show_obs_wl=1
;; order_out=0
;; max_low_enrg=
;; show_av=2
;; max_upp_enrg=
;; tsb_value=0
;; min_str=
;; A_out=0
;; intens_out=on
;; max_str=
;; allowed_out=1
;; forbid_out=1
;; min_accur=
;; min_intens=
;; submit=Retrieve+Data

(def ^:dynamic *parameters*
  "Parameter map for the line data request."
  {:spectra "H I"
   :limits_type "0"
   :low_w ""
   :upp_w ""
   :unit "1"
   :de "0"
   :format "1"
   :line_out "0"
   :remove_js "on"
   :en_unit "0"
   :output "0"
   :bibrefs "1"
   :page_size "15"
   :show_obs_wl "1"
   :order_out "0"
   :max_low_enrg ""
   :show_av "2"
   :max_upp_enrg ""
   :tsb_value "0"
   :min_str ""
   :A_out "0"
   :intens_out "on"
   :max_str ""
   :allowed_out "1"
   :forbid_out "1"
   :min_accur ""
   :min_intens ""
   :submit "Retrieve Data"
   }
  #_{"spectra"      "H+I"     ;; element
     "limits_type"  "0"
     "low_w"        ""
     "upp_w"        ""
     "unit"         "1"
     "de"           "0"
     "format"       "1"       ;; ascii format
     ;; "line_out"     "0"
     "remove_js"    "on"
     "en_unit"      "0"
     "output"       "0"
     ;; "bibrefs"      "1"
     "page_size"    "15"
     "show_obs_wl"  "1"       ;; observed wavelenghts
     "show_calc_wl" "0"       ;; theoretical wavelenghts
     ;; "unc_out"      "1"
     ;; "order_out"    "0"
     ;; "max_low_enrg" ""
     ;; "show_av"      "2"
     ;; "max_upp_enrg" ""
     ;; "tsb_value"    "0"
     ;; "min_str"      ""
     ;; "max_str"      ""
     ;; "A_out"        "0"
     ;; "intens_out"   "on"
     "allowed_out"  "1"
     ;; "forbid_out"   "1"
     "min_accur"    ""        ;; minimum line accuracy, eg AAA AA A B C
     "min_intens"   ""        ;; minimum relative intensity to return
     ;; "conf_out"     "0"      ;; show electron configuration
     ;; "term_out"     "0"      ;; show terms
     ;; "enrg_out"     "0"      ;; show transition energies
     ;; "J_out"        "0"      ;; show J
     ;; "submit" "Retrieve Data"
     })

(defn ascii
  "ASCII Table of an ion."
  ([ion] (ascii ion "I"))
  ([ion numeral]
   (-> *lines-url*
       (http/get {:query-params (assoc *parameters* :spectra (str ion " " numeral))})
       deref
       :body
       (split #"<pre>|</pre>")
       second)))

(defn write-ascii
  "Write ASCII-Table to `path`. `path` must contain a trailing /."
  [{:keys [Z element numeral] :as ion} path]
  (let [path (format (str path "%03d-%s-%s.txt") Z element numeral)]
    (with-open [w (io/writer path)]
      (println Z element numeral)
      (.write w (or (ascii element numeral) "")))))


(comment
  (let [elements (index)]
    (doseq [element (filter #(= (:numeral %) "I") elements)]
      (write-ascii element "/home/tim/src/spectra/"))))
