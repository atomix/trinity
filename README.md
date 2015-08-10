# figaro

A sweet Clojure API for [Copycat][copycat].

## Usage

```clojure
(require '[figaro.core :as figaro])
```

Create a Copycat server specifying the log, local node id, port and a set of remote nodes:

```clojure
(figaro/server 
  (figaro/mem-log) 1 5555 
  [{:id 2 :host node2 :port 5555}
   {:id 3 :host node3 :port 5555}])
```

Create a Copycat client connection to a set of servers by specifying a set of node information:

```clojure
(figaro/client
  [{:id 1 :host node1 :port 5555}
   {:id 2 :host node2 :port 5555}
   {:id 3 :host node3 :port 5555}])
```

Create a distributed atom on a path:

```clojure
(figaro/dist-atom client "register")
```

Operate on the atom:

```clojure
(figaro/get atom)
(figaro/set! atom "value")
(figaro/cas! atom "expected" "updated")
```

## Docs

API docs are available [here](http://jhalterman.github.com/figaro/docs/).

## License

Copyright © 2015 Jonathan Halterman

Distributed under the Eclipse Public License either version 1.0

[copycat]: https://github.com/kuujo/copycat