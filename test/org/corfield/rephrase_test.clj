;; Copyright © 2026 Sean Corfield, all rights reserved

(ns org.corfield.rephrase-test
  (:require [lazytest.core :refer [defdescribe expect it]]
            [org.corfield.rephrase :as sut])) ; system under test

(defdescribe repl-caught-test
  "prints exceptions to stderr"

  (it "rephrases a ClassCastException"
    (let [output
          (with-out-str
            (binding [*err* *out*]
              (sut/repl-caught (ClassCastException. "class java.lang.String cannot be cast to class java.lang.Number"))))]
      (expect (re-find #" - runtime error \(unexpected type\)\.\n$" output))
      (expect (re-find #"^Expected a number, but was given a string, at " output))))

  (it "rephrases an ArityException"
    (let [output
          (with-out-str
            (binding [*err* *out*]
              (sut/repl-caught (clojure.lang.ArityException. 0 "clojure.core//"))))]
      (expect (re-find #" - runtime error \(incorrect number of arguments\)\.\n$" output))
      (expect (re-find #"^/ was called with no arguments, but it requires at least one argument, at " output))))

  (it "does not rephrase an IllegalStateException (but still reorders its stack trace)"
    (let [output
          (with-out-str
            (binding [*err* *out*]
              (sut/repl-caught (IllegalStateException. "something went wrong"))))]
      (expect (re-find #" - runtime error \(IllegalStateException\)\.\n$" output))
      (expect (re-find #"^something went wrong, at " output)))))
