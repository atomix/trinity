(ns figaro.core
    "A sweet Clojure API for Copycat"
  (:import (net.kuujo.copycat Copycat CopycatClient CopycatServer)
           (net.kuujo.copycat.raft Members Member)
           (net.kuujo.copycat.io.transport NettyTransport)
           (net.kuujo.copycat.io.storage Log StorageLevel)
           (net.kuujo.copycat.atomic DistributedAtomicValue)))

(defn mem-log
  "Creates an in memory log."
  []
  (-> (Log/builder)
      (.withStorageLevel StorageLevel/MEMORY)
      (.build)))

(defn client
  "Returns a CopycatClient for the given nodes. Nodes should be a seq of maps containing :id, :host and :port values."
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
  "Returns a CopycatServer for the given server id, port and remote-nodes. Remote nodes should be a seq of maps
  containing :id, :host and :port values."
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

(defn close
  "Closes the Copycat client or server."
  [^Copycat copycat]
  (.close copycat))

(defn dist-atom
  "Creates a distributed atom for the client on the given path."
  [client path]
  (-> client
      (.create path DistributedAtomicValue)
      (.get)))

(defn get!
  "Gets a value from an atom."
  [^DistributedAtomicValue atom]
  (-> (.get atom)
      (.get)))

(defn set!
  "Sets a value for an atom."
  [^DistributedAtomicValue atom value]
  (-> (.set atom value)
      (.get)))

(defn cas!
  "Compares and sets a value for an atom."
  [^DistributedAtomicValue atom expected updated]
  (-> (.compareAndSet atom expected updated)
      (.get)))