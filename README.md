# org.corfield/rephrase

Rephrase exceptions.

Error messages in Clojure have been a long-standing source of frustration
for beginners. This library is an experiment in rephrasing exceptions to
make them more beginner-friendly.

The library provides two main functions:
* `org.corfield.rephrase/repl-caught` - a replacement for `clojure.main/repl-caught` that rephrases exceptions before printing them (via the `:caught` option when starting a REPL),
* `org.corfield.rephrase.nrepl/wrap-rephrase` - nREPL middleware that applies `repl-caught` to produce rephrased exceptions in nREPL sessions.

Error messages are rephrased to a single line, with the cause first, followed
by the location of the error. This makes inline display of error messages in
editors easier to read, especially if the editor normally suppresses the
second line of the standard exception report (e.g., Calva), or truncates long 
messages.

## Usage

### nREPL Middleware

This is probably the most common way to use this library.

For use with nREPL, add `org.corfield.rephrase.nrepl/wrap-rephrase` to your
list of middleware. See
[nREPL Middleware Setup](https://docs.cider.mx/cider/basics/middleware_setup.html)
in the CIDER documentation for details on how to do this.

### Starting a REPL with `:caught`

You can also use `org.corfield.rephrase/repl-caught` directly when starting a REPL, by passing it as the value of the `:caught` option. For example:

```clojure
(require '[org.corfield.rephrase :as rephrase])

(clojure.main/repl :caught rephrase/repl-caught)
```

## License

Copyright © 2026 Sean Corfield

Distributed under the [Eclipse Public License 2.0](https://www.eclipse.org/legal/epl-2.0)
