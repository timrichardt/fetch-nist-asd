(defproject fetch-nist-asd "0.1.0-SNAPSHOT"
  :description "Library to fetch data from the NIST Atomic Spectra Database."
  :url "http://github.com/timrichardt/fetch-nist-asd"
  :license {:name "MIT"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.8.0"] 
                 [http-kit "2.3.0-alpha2"]
                 [hiccup "2.0.0-alpha1"]
                 [clojure-future-spec "1.9.0-alpha17"]
                 [com.github.kyleburton/clj-xpath "1.4.11"]])
