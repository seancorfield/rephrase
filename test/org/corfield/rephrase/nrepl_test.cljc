;; Copyright © 2026 Sean Corfield, all rights reserved

(ns org.corfield.rephrase.nrepl-test
  #?(:bb ;; this is purely so ::caught/caught resolves at read time:
     (:require [org.corfield.rephrase :as caught])
     :clj
     (:require [lazytest.core :refer [defdescribe expect it]]
               [nrepl.middleware.caught :as caught]
               [org.corfield.rephrase :as rephrase]
               [org.corfield.rephrase.nrepl :as sut]))) ; system under test

#?(:bb nil
   :clj
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
         (expect (re-find #" - runtime error \(unexpected type\)\.\n$" output))
         (expect (re-find #"^Expected a number, but was given a keyword, at " output))))

     (it "rephrases an ArityException"
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
         (expect (re-find #" - runtime error \(incorrect number of arguments\)\.\n$" output))
         (expect (re-find #"^/ was called with no arguments, but it requires at least one argument, at " output))))))

(comment
  (def output "Wrong number of args (0) passed to: clojure.core//, at org.corfield.rephrase.nrepl-test/eval21928 (nrepl_test.clj:48) - runtime error (incorrect number of arguments).\n")
  )
