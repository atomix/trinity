(defproject io.atomix/trinity "0.1.0-SNAPSHOT"
  :description "A sweet little Clojure API for Atomix"
  :url "http://github.com/atomix/trinity"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [io.atomix/atomix-all "1.0.0"]
                 [io.atomix.catalyst/catalyst-netty "1.1.2"]]
  :repositories [["sonatype-nexus-snapshots" {:url "https://oss.sonatype.org/content/repositories/snapshots"}]]
  :plugins [[lein-codox "0.9.0"]]
  :codox {:output-path "target/docs/api"
          :metadata {:doc/format :markdown}
          :source-uri "http://github.com/atomix/trinity/blob/master/{filepath}#L{line}"})
