# trinity

A sweet Clojure API for [Atomix].

## Setup

Add the Leiningen dependency:

[![Clojars Project](http://clojars.org/io.atomix/trinity/latest-version.svg)](http://clojars.org/io.atomix/trinity)

## Core Usage

```clojure
(require '[trinity.core :as trinity])
```

Create an Atomix replica specifying local port to listen on and a set of remote servers that the replica should connect to:

```clojure
(trinity/replica 
  5555 
  [{:host node2 :port 5555}
   {:host node3 :port 5555}])
```

Create an Atomix client for a set of servers:

```clojure
(trinity/client
  [{:host node1 :port 5555}
   {:host node2 :port 5555}
   {:host node3 :port 5555}])
```

Open an Atomix client or replica:

```
(trinity/open! atomix)
```

Close an Atomix client or replica:

```
(trinity/close! atomix)
```

Note: Trinity functions operate sychronously by default, but many functions have async counterparts such as `open-async!` which return [CompletableFuture].

## Using Atomix Resources

#### Distributed Value

```clojure
(require '[trinity.distributed-value :as dvalue])
```

Create a distributed value on a path:

```clojure
(dvalue/create client "register")
```

Operate on the value:

```clojure
(dvalue/get value)
(dvalue/set! value "value")
(dvalue/cas! value "expected" "updated")
```

## Docs

API docs are available [here](http://atomix.io/trinity/docs/).

## License

Copyright © 2015 Jonathan Halterman

Distributed under the Eclipse Public License either version 1.0

[Atomix]: http://atomix.io/atomix
[CompletableFuture]: https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html