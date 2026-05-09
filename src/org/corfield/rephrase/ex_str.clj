;; Copyright © 2026 Sean Corfield, all rights reserved

(ns ^:no-doc org.corfield.rephrase.ex-str
  "Internal namespace for transforming exception maps into more user-friendly
   strings. Not intended for public use."
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.spec.alpha :as spec]
            [clojure.string :as str]))

(def ^:private ex-str-replacements
  (delay (->> (io/resource "org/corfield/rephrase/config.edn")
              (slurp)
              (edn/read-string))))

(defn- rephrase-ex-type
  "Return a more user-friendly name for certain exception classes."
  [clazz]
  (let [type-map (:ex-types @ex-str-replacements)]
    (get type-map (symbol clazz) clazz)))

(comment
  #_{:clj-kondo/ignore [:type-mismatch]}
  (* 1 'two)
  (/ 42 (- 13 10 3))
  )

(defn- rephrase-message
  "Return a more user-friendly message for certain exception messages."
  [msg]
  (let [inline-types (:inline-types @ex-str-replacements)
        ex-messages  (:ex-messages @ex-str-replacements)
        removals     (:removals @ex-str-replacements)]
    (-> msg
        (as-> msg
              (reduce (fn [msg [pattern replacement]]
                        (str/replace msg (re-pattern pattern) replacement))
                      msg inline-types)
          (reduce (fn [msg re]
                    (str/replace msg (re-pattern re) ""))
                  msg removals)
          (reduce (fn [msg [pattern replacement]]
                    (str/replace msg (re-pattern pattern) replacement))
                  msg ex-messages)))))

(defn- rephrase-ex-str
  "Based on clojure.main/ex-str, but with a more user-friendly format.
   Returns a string from exception data, as produced by ex-triage.
   Unlike clojure.main/ex-str, this function produces a single-line string 
   that explains the cause first, then provides the location and type."
  [{:clojure.error/keys [phase source path line column symbol class cause spec]
    :as triage-data}]
  (let [loc (str (or path source "REPL") ":" (or line 1) (if column (str ":" column) ""))
        class-name (name (or class ""))
        simple-class (when class
                       (or (first (re-find #"([^.])++$" class-name))
                           class-name))
        cause-type (if (contains? #{"Exception" "RuntimeException"} simple-class)
                     "" ;; omit, not useful
                     (str " (" simple-class ")"))
        cause (when cause (str/replace cause #"\n$" ""))]
    (case phase
      :read-source
      (format "Syntax error: %s, at (%s).%n" cause loc)

      :macro-syntax-check
      (format "Syntax error: %s, macroexpanding %sat (%s).%n"
              (if spec
                (-> (with-out-str
                      (spec/explain-out
                       (if (= spec/*explain-out* spec/explain-printer)
                         (update spec :clojure.spec.alpha/problems
                                 (fn [probs] (map #(dissoc % :in) probs)))
                         spec)))
                    (str/replace #"\n$" ""))
                cause)
              (if symbol (str symbol " ") "")
              loc)

      :macroexpansion
      (format "Unexpected error%s: %s, macroexpanding %sat (%s).%n"
              cause-type
              cause
              (if symbol (str symbol " ") "")
              loc)

      :compile-syntax-check
      (format "Syntax error%s: %s, compiling %sat (%s).%n"
              cause-type
              cause
              (if symbol (str symbol " ") "")
              loc)

      :compilation
      (format "Unexpected error%s: %s, compiling %sat (%s).%n"
              cause-type
              cause
              (if symbol (str symbol " ") "")
              loc)

      :read-eval-result
      (format "Error reading eval result%s: %s, at %s (%s).%n%"
              cause-type cause symbol loc)

      :print-eval-result
      (format "Error printing return value%s: %s, at %s (%s).%n"
              cause-type cause symbol loc)

      :execution
      (if spec
        (format "Invalid arguments to %s: %s, at (%s).%n"
                symbol
                (-> (with-out-str
                      (spec/explain-out
                       (if (= spec/*explain-out* spec/explain-printer)
                         (update spec :clojure.spec.alpha/problems
                                 (fn [probs] (map #(dissoc % :in) probs)))
                         spec)))
                    (str/replace #"\n$" ""))
                loc)
        (format "%s, at %s(%s) - runtime error%s.%n"
                cause
                (if symbol (str symbol " ") "")
                loc
                cause-type)))))

(defn to-string
  "A wrapper around `clojure.main/ex-str` that produces a more user-friendly
   string representation of an exception map."
  [ex-map]
  (-> ex-map
      (update :clojure.error/class rephrase-ex-type)
      (update :clojure.error/cause rephrase-message)
      rephrase-ex-str))
