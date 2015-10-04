# trinity

A sweet Clojure API for [Atomix][atomix].

## Usage

```clojure
(require '[trinity.core :as trinity])
```

Create a Copycat server specifying the log, local node id, port and a set of remote nodes:

```clojure
(trinity/server 
  1 5555 
  [{:id 2 :host node2 :port 5555}
   {:id 3 :host node3 :port 5555}])
```

Create a Copycat client connection to a set of servers by specifying a set of node information:

```clojure
(trinity/client
  [{:id 1 :host node1 :port 5555}
   {:id 2 :host node2 :port 5555}
   {:id 3 :host node3 :port 5555}])
```

Create a distributed atom on a path:

```clojure
(trinity/dist-atom client "register")
```

Operate on the atom:

```clojure
(trinity/get atom)
(trinity/set! atom "value")
(trinity/cas! atom "expected" "updated")
```

## Docs

API docs are available [here](http://atomix.io/trinity/docs/).

## License

Copyright © 2015 Jonathan Halterman

Distributed under the Eclipse Public License either version 1.0

[atomix]: https://github.com/atomix/atomix