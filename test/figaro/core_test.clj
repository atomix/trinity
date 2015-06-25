(ns figaro.core-test
  (:require [clojure.test :refer :all]
            [figaro.core :refer :all]))

(deftest test-client-and-server
  (testing "Creation of a client and server."
    (let [server (server (mem-log) 1 5000 nil)
          client (client (vector {:id 1 :host "localhost" :port 5000}))]

      )
    (is (= 0 1))))
