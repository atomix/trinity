(ns trinity.core
  "Functions for operating on Atomix clients, servers and replicas."
  (:import (io.atomix Atomix AtomixClient AtomixReplica)
           (io.atomix.catalyst.transport Address NettyTransport)
           (io.atomix.copycat.server.storage Storage StorageLevel)
           (java.util Collection UUID)
           (java.net InetAddress)
           (java.util.concurrent CompletableFuture)))

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

(defn client
  "Returns an `io.atomix.AtomixClient` for the `nodes` and `config`.

  * `nodes` should be a `seq` of `map`s containing `:host` and `:port` values.
  * `config` should be a `map` containing:
      * `:transport` - The `io.atomix.catalyst.transport.Transport` instance for the client to use. Defaults to `NettyTransport`."
  ^AtomixClient
  ([nodes]
   (client nodes (empty {})))
  ([nodes config]
   (let [^Collection cluster-members (map #(-> (Address. (:host %)
                                                         (:port %)))
                                          nodes)
         transport (get config :transport (NettyTransport.))
         client (-> (AtomixClient/builder cluster-members)
                    (.withTransport transport)
                    (.build))]
     client)))

(defn replica
  "Returns an `io.atomix.AtomixReplica` for the `port` and `remote-nodes`.

  * `port` should be the localhost port for the replica to listen on.
  * `remote-nodes` should be a `seq` of `map`s containing `:host` and `:port` values.
  * `config` should be a `map` containing:
      * `:storage` - The `io.atomix.copycat.server.storage.Storage` instance for the replica to use.
      * `:transport` - The `io.atomix.catalyst.transport.Transport` instance for the client to use. Defaults to `NettyTransport`."
  ^AtomixReplica
  ([port remote-nodes]
   (replica port remote-nodes (empty {})))
  ([port remote-nodes config]
   (let [localhost (-> (InetAddress/getLocalHost)
                       (.getHostName))
         local-address (Address. localhost port)
         ^Collection remote-addresses (map #(Address. (:host %) (:port %))
                                           remote-nodes)
         storage (get config :storage (Storage.))
         transport (get config :transport (NettyTransport.))
         replica (-> (AtomixReplica/builder local-address remote-addresses)
                     (.withTransport transport)
                     (.withStorage storage)
                     (.build))]
     replica)))

(defn open!
  "Opens the `atomix` client, server or replica."
  [^Atomix atomix]
  (-> (.open atomix)
      (.get)))

(defn open-async!
  "Asynchronously opens the `atomix` , server or replica."
  ^CompletableFuture
  [^Atomix atomix]
  (.open atomix))

(defn close!
  "Asynchronously closes the `atomix` , server or replica."
  [^Atomix atomix]
  (-> (.close atomix)
      (.get)))

(defn close-async!
  "Closes the `atomix` , server or replica."
  ^CompletableFuture
  [^Atomix atomix]
  (.close atomix))
