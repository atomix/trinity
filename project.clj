(defproject io.atomix/trinity "0.1.0-SNAPSHOT"
  :description "A sweet little Clojure API for Atomix"
  :url "http://github.com/atomix/trinity"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [io.atomix/atomix "0.1.0-SNAPSHOT"]
                 [io.atomix/atomix-atomic "0.1.0-SNAPSHOT"]
                 [io.atomix/atomix-collections "0.1.0-SNAPSHOT"]
                 [io.atomix/atomix-coordination "0.1.0-SNAPSHOT"]
                 [io.atomix.catalyst/catalyst-netty "1.0.0-SNAPSHOT"]]
  :repositories [["sonatype-nexus-snapshots" {:url "https://oss.sonatype.org/content/repositories/snapshots"}]]
  :plugins [[codox "0.8.13"]]
  :codox {:output-dir "target/docs/docs"
          :defaults {:doc/format :markdown}
          :src-dir-uri "http://github.com/atomix/trinity/blob/master/"
          :src-linenum-anchor-prefix "L"})
