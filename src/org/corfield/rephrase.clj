;; Copyright © 2026 Sean Corfield, all rights reserved

(ns org.corfield.rephrase
  (:require [clojure.main :refer [ex-triage ex-str]]
            [clojure.string :as str]))

(defn- rephrase-type
  "Return a more user-friendly name for certain exception classes."
  [clazz]
  (condp = clazz
    'java.lang.ClassCastException 'UnexpectedType
    clazz))

(defn- rephrase-message
  "Return a more user-friendly message for certain exception messages."
  [msg]
  (-> msg
      ;; strip unhelpful module info:
      (str/replace #" \(.*is in unnamed module.*\)$" "")
      ;; rephrase common confusing exception messages:
      (str/replace #"^class (.*) cannot be cast to class (.*)$"
                   "Expected $2 but got $1")
      ;; strip common package names:
      (str/replace #"clojure\.lang\." "")
      (str/replace #"java\.lang\." "")))

(defn- rephrase-err->msg
  "Helper to return an error message string from an exception."
  [^Throwable e]
  (-> e
      Throwable->map
      (update-in [:via 0 :type] rephrase-type)
      (update-in [:via 0 :message] rephrase-message)
      ex-triage
      ex-str))

(defn repl-caught
  "A replacement for `clojure.main/repl-caught` that rephrases exceptions
   before printing them. Can be used via the `:caught` option when starting
   a REPL."
  [e]
  (binding [*out* *err*]
    (print (rephrase-err->msg e))
    (flush)))
