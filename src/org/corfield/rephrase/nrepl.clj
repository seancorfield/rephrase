;; Copyright © 2026 Sean Corfield, all rights reserved

(ns org.corfield.rephrase.nrepl
  (:require [nrepl.middleware :refer [set-descriptor!]]
            [nrepl.middleware.caught :as caught]
            [org.corfield.rephrase :as rephrase]))

(set! *warn-on-reflection* true)

(defn wrap-rephrase
  "nREPL middleware that rephrases exceptions to be more beginner-friendly."
  [h]
  (fn [msg]
    (-> msg
        (assoc :nrepl.middleware.caught/caught
               `rephrase/repl-caught)
        (h))))

(set-descriptor! #'org.corfield.rephrase.nrepl/wrap-rephrase
                 {:expects #{#'caught/wrap-caught}})
