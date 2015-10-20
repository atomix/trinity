(ns trinity.core
    "A sweet Clojure API for Atomix"
  (:import (io.atomix Atomix AtomixClient AtomixReplica)
           (io.atomix.catalyst.transport Address NettyTransport)
           (io.atomix.copycat.server.storage Storage StorageLevel)
           (io.atomix.atomic DistributedAtomicValue)
           (java.util UUID Collection)))

; (str (System/getProperty "user.dir") "/logs/" (UUID/randomUUID))
(defn disk-storage
  [path]
  (-> (Storage/builder)
      (.withDirectory path)
      (.build)))

(defn mem-storage []
  (Storage. StorageLevel/MEMORY))

(defn client
  "Returns an AtomixClient for the `nodes`. `nodes` should be a `seq` of `map`s containing `:host` and `:port`
   values."
  ^AtomixClient
  [nodes]
  (let [^Collection cluster-members (map #(-> (Address. (:host %)
                                                        (:port %)))
                                         nodes)
        transport (NettyTransport.)
        client (-> (AtomixClient/builder cluster-members)
                   (.withTransport transport)
                   (.build)
                   (.open)
                   (.get))]
    client))

(defn atomix
  "Returns an `Atomix` for the `port` and `remote-nodes`. `remote-nodes` should be a `seq` of `map`s
  containing `:host` and `:port` values."
  ^Atomix
  [storage port remote-nodes]
  (let [localhost (-> (java.net.InetAddress/getLocalHost)
                      (.getHostName))
        local-address (Address. localhost port)
        ^Collection remote-addresses (map #(Address. (:host %) (:port %))
                                          remote-nodes)
        transport (NettyTransport.)
        atomix (-> (AtomixReplica/builder local-address remote-addresses)
                    (.withTransport transport)
                    (.withStorage storage)
                    (.build)
                    (.open)
                    (.get))]
    atomix))

(defn close!
  "Closes the `atomix` client or server."
  [^Atomix atomix]
  (-> (.close atomix)
      (.get)))

(defn dist-atom
  "Creates a distributed atom for the `client` on the `path`."
  [client path]
  (-> client
      (.create path DistributedAtomicValue)
      (.get)))

(defn get
  "Gets a value from the `atom`."
  [^DistributedAtomicValue atom]
  (-> (.get atom)
      (.get)))

(defn set!
  "Sets the `value` for the `atom`."
  [^DistributedAtomicValue atom value]
  (-> (.set atom value)
      (.get)))

(defn cas!
  "Atomically compares and sets the `updated` value when the `expected` value matches the current value of the `atom`."
  [^DistributedAtomicValue atom expected updated]
  (-> (.compareAndSet atom expected updated)
      (.get)))