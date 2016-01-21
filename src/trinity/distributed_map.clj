(ns trinity.distributed-map
  "Functions for operating on Atomix DistributedMaps."
  (:import (io.atomix.collections DistributedMap)
           (io.atomix Atomix)
           (java.util.concurrent CompletableFuture)))

(defn get
  "Gets a value from the `dmap` for the `key`."
  [^DistributedMap dmap key]
  (-> (.get dmap key)
      (.get)))

(defn get-async
  "Gets a value asynchronously from the `dmap` for the `key`, returning a `CompletableFuture`."
  ^CompletableFuture
  [^DistributedMap dmap key]
  (.get dmap key))

(defn put!
  "Puts the `value` for the `dmap`."
  [^DistributedMap dmap key value]
  (-> (.put dmap key value)
      (.get)))

(defn put-async!
  "Puts the `value` asynchronously for the `dmap`, returning a `CompletableFuture`."
  ^CompletableFuture
  [^DistributedMap dmap key value]
  (.put dmap key value))

(defn remove!
  "Removes the entry for the `key` in the `dmap`."
  [^DistributedMap dmap key]
  (-> (.remove dmap key)
      (.get)))

(defn remove-async!
  "Removes the entry asynchronously for the `key` in the `dmap`, returning a `CompletableFuture`."
  ^CompletableFuture
  [^DistributedMap dmap key]
  (.remove dmap key))