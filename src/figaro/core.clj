(ns figaro.core
    "A sweet Clojure API for Copycat"
  (:import (net.kuujo.copycat Copycat CopycatClient CopycatReplica)
           (net.kuujo.copycat.raft Members Member)
           (net.kuujo.copycat.io.transport NettyTransport)
           (net.kuujo.copycat.io.storage Storage)
           (net.kuujo.copycat.atomic DistributedAtomicValue)))

(defn client
  "Returns a CopycatClient for the `nodes`. `nodes` should be a `seq` of `map`s containing `:id`, `:host` and `:port`
   values."
  ^CopycatClient
  [nodes]
  (let [cluster-members (map #(-> (Member. (:id %)
                                           (:host %)
                                           (:port %)))
                             nodes)
        cluster (-> (Members/builder)
                    (.withMembers cluster-members)
                    (.build))
        transport (NettyTransport.)
        client (-> (CopycatClient/builder)
                   (.withTransport transport)
                   (.withMembers cluster)
                   (.build)
                   (.open)
                   (.get))]
    client))

(defn copycat
  "Returns a `Copycat` for the `id`, `port` and `remote-nodes`. `remote-nodes` should be a `seq` of `map`s
  containing `:id`, `:host` and `:port` values."
  ^Copycat
  [id port remote-nodes]
  (let [localhost (-> (java.net.InetAddress/getLocalHost)
                      (.getHostName))
        local-member (Member. id localhost port)
        remote-members (map #(Member. (:id %) (:host %) (:port %))
                            remote-nodes)
        all-members (conj remote-members local-member)
        members (-> (Members/builder)
                    (.withMembers all-members)
                    (.build))
        transport (NettyTransport.)
        storage (Storage.)
        copycat (-> (CopycatReplica/builder)
                    (.withTransport transport)
                    (.withMemberId id)
                    (.withMembers members)
                    (.withStorage storage)
                    (.build)
                    (.open)
                    (.get))]
    copycat))

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