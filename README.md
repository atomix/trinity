# figaro

A sweet Clojure API for [Copycat][copycat].

## Usage

Create a client connection to a set of Copycat servers by specifying the server ID, host and port:

```clojure
(figaro.core/connect
  [[1 node1 5555]
   [2 node2 5555]
   [3 node3 5555]])
```

Create a distributed atom on a path:

```clojure
(figaro.core/atom client "register")
```

Operate on the atom:

```clojure
(figaro.core/get! atom)
(figaro.core/set! atom "value")
(figaro.core/cas! atom "expected" "updated")
```

## License

Copyright © 2015 Jonathan Halterman

Distributed under the Eclipse Public License either version 1.0

[copycat]: https://github.com/kuujo/copycat