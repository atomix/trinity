(ns trinity.distributed-value
  "Functions for operating on Atomix DistributedValues."
  (:import (io.atomix.atomic DistributedAtomicValue)
           (io.atomix Atomix)
           (java.util.concurrent CompletableFuture)))

(defn create
  "Creates a distributed value for the `atomix` instance on the resource `key`."
  [^Atomix atomix key]
  (-> atomix
      (.create key DistributedAtomicValue)
      (.get)))

(defn get
  "Gets a value from the `dvalue`."
  [^DistributedAtomicValue dvalue]
  (-> (.get dvalue)
      (.get)))

(defn get-async
  "Gets a value asynchronously from the `dvalue`, returning a `CompletableFuture`."
  ^CompletableFuture
  [^DistributedAtomicValue dvalue]
  (.get dvalue))

(defn set!
  "Sets the `value` for the `dvalue`."
  [^DistributedAtomicValue dvalue value]
  (-> (.set dvalue value)
      (.get)))

(defn set-async!
  "Sets the `value` asynchronously for the `dvalue`."
  ^CompletableFuture
  [^DistributedAtomicValue dvalue value]
  (.set dvalue value))

(defn cas!
  "Compares and sets the `updated` value when the `expected` value matches the current value of the `dvalue`."
  [^DistributedAtomicValue dvalue expected updated]
  (-> (.compareAndSet dvalue expected updated)
      (.get)))

(defn cas-async!
  "Compares and sets the `updated` value asynchronously when the `expected` value matches the current value of the `dvalue`."
  ^CompletableFuture
  [^DistributedAtomicValue dvalue expected updated]
  (.compareAndSet dvalue expected updated))