(ns figaro.core
    "A sweet Clojure API for Copycat"
  (:import (net.kuujo.copycat Copycat CopycatClient CopycatServer)
           (net.kuujo.copycat.cluster NettyMembers NettyMember NettyCluster)
           (net.kuujo.copycat.raft.log Log StorageLevel)
           (net.kuujo.copycat.atomic AsyncReference)))

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
  (let [cluster-members (map #(-> (NettyMember/builder)
                                  (.withId (:id %))
                                  (.withHost (:host %))
                                  (.withPort (:port %))
                                  .build)
                             nodes)
        cluster (-> (NettyMembers/builder)
                    (.withMembers cluster-members)
                    (.build))
        client (-> (CopycatClient/builder)
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
  (let [local-member (-> (NettyMember/builder)
                         (.withId id)
                         (.withHost (-> (java.net.InetAddress/getLocalHost)
                                        (.getHostName)))
                         (.withPort port)
                         .build)
        remote-members (map #(-> (NettyMember/builder)
                                 (.withId (:id %))
                                 (.withHost (:host %))
                                 (.withPort (:port %))
                                 .build)
                            remote-nodes)
        cluster-members (conj remote-members local-member)
        cluster (-> (NettyCluster/builder)
                    (.withMemberId id)
                    (.withMembers cluster-members)
                    (.build))
        server (-> (CopycatServer/builder)
                   (.withCluster cluster)
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
      (.create path AsyncReference)
      (.get)))

(defn get!
  "Gets a value from an atom."
  [^AsyncReference atom]
  (-> (.get atom)
      (.get)))

(defn set!
  "Sets a value for an atom."
  [^AsyncReference atom value]
  (-> (.set atom value)
      (.get)))

(defn cas!
  "Compares and sets a value for an atom."
  [^AsyncReference atom expected updated]
  (-> (.compareAndSet atom expected updated)
      (.get)))