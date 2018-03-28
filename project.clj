(defproject lein-jar-git-info "0.0.0"
  :description "A Leiningen plugin that bakes a git information into JAR file"
  :url "https://github.com/druids/lein-jar-git-info"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}

  :dependencies [[clj-jgit "0.8.10"]]

  :eval-in-leiningen true

  :profiles {:dev {:plugins [[lein-cloverage "1.0.10"]
                             [lein-kibit "0.1.6"]
                             [jonase/eastwood "0.2.5"]]

                   :dependencies [[org.clojure/clojure "1.9.0"]]}})
