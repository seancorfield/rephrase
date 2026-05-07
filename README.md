# org.corfield/rephrase

Rephrase exceptions.

Error messages in Clojure have been a long-standing source of frustration
for beginners. This library is an experiment in rephrasing exceptions to
make them more beginner-friendly.

The library provides two main functions:
* `org.corfield.rephrase/repl-caught` - a replacement for `clojure.main/repl-caught` that rephrases exceptions before printing them (via the `:caught` option when starting a REPL),
* `org.corfield.rephrase.nrepl/wrap-rephrase` - nREPL middleware that applies `repl-caught` to produce rephrased exceptions in nREPL sessions.

## Usage

## License

Copyright © 2026 Sean Corfield

Distributed under the [Eclipse Public License 2.0](https://www.eclipse.org/legal/epl-2.0)
