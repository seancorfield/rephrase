;; Copyright © 2026 Sean Corfield, all rights reserved

(ns ^:no-doc org.corfield.rephrase.specs
  (:require [clojure.spec.alpha :as s]))

(set! *warn-on-reflection* true)

(s/def ::ex-types
  (s/map-of symbol? string?))

(s/def ::inline-types
  (s/coll-of (s/and vector? #(= 2 (count %))
                    (fn [[pattern replacement]]
                      (and (string? pattern) (string? replacement))))))

(s/def ::removals
  (s/coll-of string?))

(s/def ::ex-messages
  (s/coll-of (s/and vector? #(<= 2 (count %) 3)
                    (fn [[pattern replacement opt-class-symbol]]
                      (and (string? pattern) (string? replacement)
                           (or (nil? opt-class-symbol) (symbol? opt-class-symbol)))))))

(s/def ::config
  (s/keys :opt-un [::ex-types ::inline-types ::removals ::ex-messages]))
