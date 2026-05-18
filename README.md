# org.corfield/rephrase 

[![Clojure CI Release](https://github.com/seancorfield/rephrase/actions/workflows/test-and-release.yml/badge.svg)](https://github.com/seancorfield/rephrase/actions/workflows/test-and-release.yml) [![Clojure CI Develop](https://github.com/seancorfield/rephrase/actions/workflows/test-and-snapshot.yml/badge.svg)](https://github.com/seancorfield/rephrase/actions/workflows/test-and-snapshot.yml) [![Clojure CI Pull Request](https://github.com/seancorfield/rephrase/actions/workflows/test.yml/badge.svg)](https://github.com/seancorfield/rephrase/actions/workflows/test.yml)

Rephrase exceptions.

Error messages in Clojure have been a long-standing source of frustration
for beginners. This library is an experiment in rephrasing exceptions to
make them more beginner-friendly.

The library provides two main functions:
* `org.corfield.rephrase/repl-caught` - a replacement for `clojure.main/repl-caught` that rephrases exceptions before printing them (via the `:caught` option when starting a REPL),
* `org.corfield.rephrase.nrepl/wrap-rephrase` - nREPL middleware that applies `repl-caught` to produce rephrased exceptions in nREPL sessions.

There is also a helper function that applications or tools might use:
* `org.corfield.rephrase/rephrase-err->msg` - a replacement for `clojure.main/err->msg` that takes an exception and returns a rephrased error message string.

Error messages are rephrased to a single line, with the cause first, followed
by the location of the error. This makes inline display of error messages in
editors easier to read, especially if the editor normally suppresses the
second line of the standard exception report (e.g., Calva), or truncates long 
messages.

## Usage

[![Clojars](https://img.shields.io/badge/clojars-org.corfield/rephrase_0.1.0--SNAPSHOT-blue.svg?logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAMAAABEpIrGAAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAAPoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAABjFBMVEUAAAAdCh0qDikdChwAAAAnDSY0EjM2FjUnDiYnDSYnDSYpDigyEDEEAQRGNUb///////8mDSYAAAAAAAAAAAAFAgUqEyoAAAAAAAAAAAAFAgUAAABXU1c2FjVMx+dQx+f///////9Nx+b////4/f6y4vRPt+RQtOT///9Qt+P///8oDSey4vRQr9/////3/P5hzelNx+dNx+dNx+f///8AAAAuDy0zETIAAAAoDScAAAAAAAARBREAAAAvDy40ETMwEC9gSF+Ne42ilKKuoK6Rg5B5ZXlaP1o4Gzf///9nTWZ4YncyEDF/bn/8/Pz9/P339/c1FTUlDCRRM1AbCRtlS2QyEDEuDy1gRWAxEDAzETIwEC/g4OAvDy40EjOaiZorDiq9sbzNyM3UzdQyEDE0ETMzETKflZ/UzdQ5Fzmu4fNYyuhNx+dPt+RLu9xQyOhBbo81GTuW2vCo4PJNx+c4MFE5N1lHiLFEhKQyEDGDboMzETI5Fjh5bXje2d57aHrIw8jc2NyWhJUrDioxe9o4AAAAPnRSTlMAkf+IAQj9+e7n6e31RtqAD/QAAAED+A0ZEQ8DwvkLBsmcR4aG8+cdAD6C8/MC94eP+qoTrgH+/wj1HA8eEvpXOCUAAAABYktHRA8YugDZAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAB3RJTUUH3wcHFjou4Z/shwAAAUpJREFUOMul0/VTwzAUB/AAwyW4y3B3h8EDNuTh7u6UDHcd8I+TbHSjWdrjju/1h77kc+3Lu5aQvyakF/r6B5wu1+DQMEBomLRtG0EpozYDCEccA4iIjIqOiY0bB5iYxHgZ4FQCpYneKmmal0aQPMOXZnUAvJhLkbpInf8NFtKCTrGImK6DJcTlDGl/BXGV6oCsrSNIYAM3aQDwl2xJYBtBB5lZAuyYgWzY3YMcNcjN2wc4EGMEFTg8+hlyfgEenygAj71Q9FBExH0wKC4p1bRTJlJWXqEAVNM05ovbXfkPAHBmAUQPAGaAsXMBLiwA8z3h0gRcsWsObuAWLJu8Awb3ZoB5T8EvS/CgBo9Y5Z8TPwXBJwlUI9Ia/yRrEZ8lID71Olrf0MiamkkL4kurDEjba+C/e2sninR0wrsH8eMTvrqIWbodjh7jyjdtCY3Aniz4jwAAACV0RVh0ZGF0ZTpjcmVhdGUAMjAxNS0wNy0wN1QyMjo1ODo0NiswMjowMCgWtSoAAAAldEVYdGRhdGU6bW9kaWZ5ADIwMTUtMDctMDdUMjI6NTg6NDYrMDI6MDBZSw2WAAAAAElFTkSuQmCC)](https://clojars.org/org.corfield/rephrase)
[![Slack](https://img.shields.io/badge/slack-rephrase-orange.svg?logo=slack)](https://clojurians.slack.com/app_redirect?channel=rephrase)
[![Join Slack](https://img.shields.io/badge/slack-join_clojurians-orange.svg?logo=slack)](http://clojurians.net)

Add the following dependency to your project (or as a global dependency
in your user-level `deps.edn` or `profiles.clj` file):

```clojure
org.corfield/rephrase {:mvn/version "0.1.0-SNAPSHOT"}
```

This snapshot version will be updated frequently as I get feedback from the
community. Once it reaches a sufficiently stable state, I will release a 1.0.0 
version.

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

### Customization

You can add more mappings by adding `org/corfield/rephrase-user.edn` to your 
classpath with the same structure as `config.edn`.
See [the source](https://github.com/seancorfield/rephrase/blob/main/resources/org/corfield/rephrase/config.edn)
for details.

Configuration is available under four keys in the EDN file:
* `:ex-types` - a hash map from exception class names (as symbols) to friendly names (as strings); this is used to rephrase the exception type itself in the error message.
* `:inline-types` - a vector of pairs: each pair is typically a class name (as a regex string) and a friendly name (as a replacement string); this is used to replace occurrences of the class name in the exception message with the friendly name.
* `:removals` - a vector of regex strings; any occurrence of these strings in the exception message will be removed.
* `:ex-messages` - a vector of pairs (but see below): each pair is a regex string and a replacement string; this is used to rephrase specific messages to more beginner-friendly versions.

The `:ex-types` mapping is applied independently to the exception type.

The exception message is rephrased by mapping the `:inline-types` first, 
then applying the `:removals`, and finally applying the `:ex-messages` 
replacements. All three of these are applied in the order they are defined
in the configuration files (default first, then any user mappings), so more
specific mappings should come first, then more general ones. All mappings
are applied -- rephrasing does not stop after the first match.

The pairs in `:ex-messages` may have an optional third element, a symbol, that
indicates the mapping should only be applied to messages of a specific exception 
type. If the symbol is present, the mapping will only be applied if the 
original exception type matches the symbol (i.e., before rephrasing via `:ex-types`).
This allows for more specific rephrasings that only apply to certain exception types, while still allowing more general rephrasings to apply to all messages.

## Inspiration

There have been a lot of discussions and libraries started around this topic.
I've toyed with the idea of rephrasing exceptions for several years, and have
started to write a library like this more than once.

[Adrian Smith](https://github.com/phronmophobic/) provided a long list of
links on [Slack](https://clojurians.slack.com/archives/C03S1KBA2/p1756162447266729)
that has been great background reading for this project.

Much of the initial mapping of class names and rephrasing of exception messages
comes from [Babel](https://github.com/Clojure-Intro-Course/babel).

## License

Copyright © 2026 Sean Corfield

Distributed under the [Eclipse Public License 2.0](https://www.eclipse.org/legal/epl-2.0)
