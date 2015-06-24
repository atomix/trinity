(ns figaro.core
    "A sweet Clojure API for Copycat"
  (:import (net.kuujo.copycat CopycatClient)
           (net.kuujo.copycat.cluster NettyMembers NettyMember)
           (net.kuujo.copycat.atomic AsyncReference)))

(defn connect
  "Returns a CopycatClient for the given nodes. Nodes should be a nested seq of [id host port] values."
  [nodes]
  (let [cluster-members (map (let [[id host port] %]
                               (-> (NettyMember/builder)
                                   (.withId id)
                                   (.withHost host)
                                   (.withPort port)
                                   .build))
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

(defn close
  "Closes the client."
  [client]
  (.close client))

(defn atom
  "Creates a distributed atom on the given path."
  [client path]
  (-> client
      (.create path AsyncReference)
      (.get)))

(defn get!
  "Gets a value from an atom."
  [^AsyncReference atom]
  (-> (.get atom) (.get)))

(defn set!
  "Sets a value for an atom."
  [^AsyncReference atom value]
  (-> (.set atom value) (.get)))

(defn cas!
  "Compares and sets a value for an atom."
  [^AsyncReference atom expected updated]
  (-> (.compareAndSet atom expected updated) (.get)))