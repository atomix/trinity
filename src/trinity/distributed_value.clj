(ns trinity.distributed-value
  "Functions for operating on Atomix DistributedValues."
  (:import (io.atomix.variables DistributedValue)
           (io.atomix Atomix)
           (java.util.concurrent CompletableFuture)))

(defn get
  "Gets a value from the `dvalue`."
  [^DistributedValue dvalue]
  (-> (.get dvalue)
      (.get)))

(defn get-async
  "Gets a value asynchronously from the `dvalue`, returning a `CompletableFuture`."
  ^CompletableFuture
  [^DistributedValue dvalue]
  (.get dvalue))

(defn set!
  "Sets the `value` for the `dvalue`."
  [^DistributedValue dvalue value]
  (-> (.set dvalue value)
      (.get)))

(defn set-async!
  "Sets the `value` asynchronously for the `dvalue`, returning a `CompletableFuture`."
  ^CompletableFuture
  [^DistributedValue dvalue value]
  (.set dvalue value))

(defn cas!
  "Compares and sets the `updated` value when the `expected` value matches the current value of the `dvalue`."
  [^DistributedValue dvalue expected updated]
  (-> (.compareAndSet dvalue expected updated)
      (.get)))

(defn cas-async!
  "Compares and sets the `updated` value asynchronously when the `expected` value matches the current value of the
  `dvalue`, returning a `CompletableFuture`."
  ^CompletableFuture
  [^DistributedValue dvalue expected updated]
  (.compareAndSet dvalue expected updated))