;; Copyright © 2026 Sean Corfield, all rights reserved

(ns org.corfield.rephrase
  (:require [clojure.main :refer [ex-triage]]
            [org.corfield.rephrase.ex-str :as ex-str]))

(set! *warn-on-reflection* true)

(defn- rephrase-err->msg
  "Helper to return an error message string from an exception.
   Copied from `clojure.main`, updated to call our custom 'to string'."
  [^Throwable e]
  (-> e
      Throwable->map
      ex-triage
      ex-str/to-string))

(defn repl-caught
  "A replacement for `clojure.main/repl-caught` that rephrases exceptions
   before printing them. Can be used via the `:caught` option when starting
   a REPL."
  [e]
  (binding [*out* *err*]
    (print (rephrase-err->msg e))
    (flush)))
