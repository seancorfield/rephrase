;; Copyright © 2026 Sean Corfield, all rights reserved

(ns org.corfield.rephrase.nrepl-test
  (:require [lazytest.core :refer [defdescribe expect it]]
            [nrepl.middleware.caught :as caught]
            [org.corfield.rephrase :as rephrase]
            [org.corfield.rephrase.nrepl :as sut])) ; system under test

(defdescribe wrap-rephrase-test
  "adds a wrapper that rephrases exceptions"

  (it "adds a ::caught/caught key to the message"
    (let [msg ((sut/wrap-rephrase identity) {:op "eval" :code "(+ 1 :one)"})]
      (expect (contains? msg ::caught/caught))
      (expect (= `rephrase/repl-caught (::caught/caught msg)))))

  (it "rephrases a ClassCastException"
    (let [output
          (with-out-str
            (binding [*err* *out*]
              ((sut/wrap-rephrase
                (caught/wrap-caught
                 (fn [{:keys [op code] ::caught/keys [caught-fn]}]
                   (if (= "eval" op)
                     (try (eval (read-string code))
                          (expect false "Expected an exception to be thrown")
                          (catch Exception e (caught-fn e)))
                     (expect false "Unexpected op")))))
               {:op "eval" :code "(+ 1 :one)"})))]
      (expect (re-find #"^Execution error \(UnexpectedType\).*" output))
      (expect (re-find #".*Expected Number but got Keyword\n$" output))))

  (it "does not rephrase an ArityException"
    (let [output
          (with-out-str
            (binding [*err* *out*]
              ((sut/wrap-rephrase
                (caught/wrap-caught
                 (fn [{:keys [op code] ::caught/keys [caught-fn]}]
                   (if (= "eval" op)
                     (try (eval (read-string code))
                          (expect false "Expected an exception to be thrown")
                          (catch Exception e (caught-fn e)))
                     (expect false "Unexpected op")))))
               {:op "eval" :code "(/)"})))]
      (expect (re-find #"^Execution error \(ArityException\).*" output))
      (expect (re-find #".*Wrong number of args \(0\) passed to: clojure.core//\n$" output)))))
