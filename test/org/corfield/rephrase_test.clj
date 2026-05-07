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
      (expect (re-find #"^Execution error \(UnexpectedType\).*" output))
      (expect (re-find #".*Expected Number but got String\n$" output))))

  (it "does not rephrase an ArityException"
    (let [output
          (with-out-str
            (binding [*err* *out*]
              (sut/repl-caught (clojure.lang.ArityException. 0
                                (str "Execution error (ArityException) at org.corfield.rephrase-test/...\n"
                                     "Wrong number of args (0) passed to: clojure.core//\n")))))]
      (expect (re-find #"^Execution error \(ArityException\).*" output))
      (expect (re-find #".*Wrong number of args \(0\) passed to: clojure.core//\n$" output)))))
