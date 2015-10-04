(ns trinity.core-test
  (:require [clojure.test :refer :all]
            [trinity.core :refer :all]))

(deftest test-client-and-server
  (testing "Creation of a client and server."
    (let [atomix (atomix (mem-storage) 5000 nil)
          client (client (vector {:host "localhost" :port 5000}))]
      )
    (is (= 0 1))))
