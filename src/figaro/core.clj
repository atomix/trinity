(ns figaro.core
    "A sweet Clojure API for Copycat"
  (:import (net.kuujo.copycat Copycat CopycatClient CopycatServer)
           (net.kuujo.copycat.raft Members Member)
           (net.kuujo.copycat.io.transport NettyTransport)
           (net.kuujo.copycat.io.storage Log StorageLevel)
           (net.kuujo.copycat.atomic DistributedAtomicValue)))

(defn mem-log
  "Returns an in memory log."
  []
  (-> (Log/builder)
      (.withStorageLevel StorageLevel/MEMORY)
      (.build)))

(defn client
  "Returns a CopycatClient for the `nodes`. `nodes` should be a `seq` of `map`s containing `:id`, `:host` and `:port`
   values."
  ^CopycatClient
  [nodes]
  (let [cluster-members (map #(-> (Member/builder)
                                  (.withId (:id %))
                                  (.withHost (:host %))
                                  (.withPort (:port %))
                                  .build)
                             nodes)
        cluster (-> (Members/builder)
                    (.withMembers cluster-members)
                    (.build))
        transport (.build (NettyTransport/builder))
        client (-> (CopycatClient/builder)
                   (.withTransport transport)
                   (.withMembers cluster)
                   (.build)
                   (.open)
                   (.get))]
    client))

(defn server
  "Returns a `CopycatServer` for the server `id`, `port` and `remote-nodes`. `remote-nodes` should be a `seq` of `map`s
  containing `:id`, `:host` and `:port` values."
  ^CopycatServer
  [log id port remote-nodes]
  (let [local-member (-> (Member/builder)
                         (.withId id)
                         (.withHost (-> (java.net.InetAddress/getLocalHost)
                                        (.getHostName)))
                         (.withPort port)
                         .build)
        remote-members (map #(-> (Member/builder)
                                 (.withId (:id %))
                                 (.withHost (:host %))
                                 (.withPort (:port %))
                                 .build)
                            remote-nodes)
        all-members (conj remote-members local-member)
        members (-> (Members/builder)
                    (.withMembers all-members)
                    (.build))
        transport (.build (NettyTransport/builder))
        server (-> (CopycatServer/builder)
                   (.withTransport transport)
                   (.withMemberId id)
                   (.withMembers members)
                   (.withLog log)
                   (.build)
                   (.open)
                   (.get))]
    server))

(defn close!
  "Closes the `copycat` client or server."
  [^Copycat copycat]
  (.close copycat))

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