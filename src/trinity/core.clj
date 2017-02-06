(ns trinity.core
  "Functions for operating on Atomix clients, servers and replicas."
  (:import (io.atomix Atomix AtomixClient AtomixReplica)
           (io.atomix.catalyst.transport Address)
           (io.atomix.catalyst.transport.netty NettyTransport)
           (io.atomix.copycat.server.storage Storage StorageLevel)
           (java.util Collection UUID)
           (java.net InetAddress)
           (java.util.concurrent CompletableFuture)
           (io.atomix.collections DistributedMap)
           (io.atomix.variables DistributedValue)))

(defn disk-storage
  "Returns a `io.atomix.copycat.server.storage.Storage` instance for the `config`.

  * `config` (Optional) should be a `map` containing:
      * `:path` - The path to store logs in. Defaults to `[user.dir]/logs/`"
  ([]
   (disk-storage (empty {})))
  ([config]
   (-> (Storage/builder)
       (.withDirectory (str (get config :path (str (System/getProperty "user.dir") "/logs/" (UUID/randomUUID)))))
       (.build))))

(defn mem-storage []
  (Storage. StorageLevel/MEMORY))

(defn- addresses-for-nodes
  "Returns Collection of Addresses for the given nodes.

  * `nodes` - A `seq` of `map`s containing `:host` and `:port` values."
  ^Collection
  [nodes]
  (map #(Address. (:host %) (:port %))
       nodes))

(defn client
  "Returns an `io.atomix.AtomixClient` for the `nodes` and `config`.

  * `config` - A `map` containing:
      * `:transport` - The `io.atomix.catalyst.transport.Transport` instance for the client to use. Defaults to `NettyTransport`."
  ^AtomixClient
  ([]
   (client (empty {})))
  ([config]
   (let [transport (get config :transport (NettyTransport.))
         client (-> (AtomixClient/builder)
                    (.withTransport transport)
                    (.build))]
     client)))

(defn replica
  "Returns an `AtomixReplica` for the `port` and `config`.

  * `port` - The localhost port for the replica to listen on.
  * `config` - A `map` containing:
      * `:storage` - The `io.atomix.copycat.server.storage.Storage` instance for the replica to use.
      * `:transport` - The `io.atomix.catalyst.transport.Transport` instance for the client to use. Defaults to `NettyTransport`."
  ^AtomixReplica
  ([port nodes]
   (replica port nodes (empty {})))
  ([port nodes config]
   (let [localhost (-> (InetAddress/getLocalHost)
                       (.getHostName))
         local-address (Address. localhost port)
         storage (get config :storage (Storage.))
         transport (get config :transport (NettyTransport.))
         replica (-> (AtomixReplica/builder local-address)
                     (.withTransport transport)
                     (.withStorage storage)
                     (.build))]
     replica)))

(defn connect!
  "Connects the client to a cluster.

  * `client` - The client to connect to the cluster.
  * `nodes` - A `seq` of `map`s containing `:host` and `:port` values."
  [^AtomixClient client nodes]
  (-> (.connect client (addresses-for-nodes nodes))))

(defn bootstrap-async!
  "Asynchronously bootstraps the `replica`.

  `nodes` - The nodes to bootstrap the replica into. A `seq` of `map`s containing `:host` and `:port` values.
  If not given, the replica is bootstrapped as a single node cluster."
  ^CompletableFuture
  ([^AtomixReplica replica]
   (.bootstrap replica))
  ([^AtomixReplica replica nodes]
    (.bootstrap replica (addresses-for-nodes nodes))))

(defn bootstrap
  "Bootstraps the `replica`.

  `nodes` - The nodes to bootstrap the replica into. A `seq` of `map`s containing `:host` and `:port` values.
  If not given, the replica is bootstrapped as a single node cluster."
  ([^AtomixReplica replica]
   (-> (bootstrap-async! replica)
       (.get)))
  ([^AtomixReplica replica nodes]
   (-> (bootstrap-async! replica nodes)
       (.get))))

(defn join
  "Joins the `replica` to an existing cluster.

  * replica The replica to join to a cluster
  * `nodes` - A `seq` of `map`s containing `:host` and `:port` values indicating the nodes to join."
  [^AtomixReplica replica nodes]
  (-> (.bootstrap replica)
      (.get)))

(defn join-async!
  "Asynchronously joins the `replica` to an existing cluster.

  * `replica` - The replica to join to a cluster
  * `nodes` - A `seq` of `map`s containing `:host` and `:port` values indicating the nodes to join."
  ^CompletableFuture
  [^AtomixReplica replica nodes]
  (bootstrap replica))

(defn close!
  "Closes the `client`."
  [^AtomixClient client]
  (-> (.close client)
      (.get)))

(defn close-async!
  "Asynchronously closes the `client`."
  ^CompletableFuture
  [^AtomixClient client]
  (.close client))

(defn get-value
  "Gets a distributed value for the `atomix` instance on the resource `key`."
  ^DistributedValue
  [^Atomix atomix key]
  (-> atomix
      (.getValue key)
      (.get)))

(defn get-value-async
  "Asynchronously gets a distributed value for the `atomix` instance on the resource `key`."
  ^CompletableFuture
  [^Atomix atomix key]
  (-> atomix
      (.getValue key)))

(defn get-map
  "Gets a distributed map for the `atomix` instance on the resource `key`."
  ^DistributedMap
  [^Atomix atomix key]
  (-> atomix
      (.getMap key)
      (.get)))

(defn get-map-async
  "Asynchronously gets a distributed map for the `atomix` instance on the resource `key`."
  ^CompletableFuture
  [^Atomix atomix key]
  (-> atomix
      (.getMap key)))
