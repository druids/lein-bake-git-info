(ns leiningen.bake-git-info
  (:require
    [clojure.edn :as edn]
    [clojure.java.io :as io]
    [clojure.string :as string]
    [clj-jgit.porcelain :as p]
    [clj-jgit.querying :as q]
    [clj-time.coerce :as c]
    [clj-time.format :refer [unparse formatter]]
    [leiningen.core.main :refer [info warn]])
  (:import
    java.io.File
    (java.nio.file StandardCopyOption
                   Files
                   FileSystems
                   Paths)))


(def path-join (partial string/join File/separator))


(defn- git-tag-list
  "Lists the tags in a repo, returning them as a seq of strings.
   clj-jgit.porcelain doesn't contain git-tag-list, no idea why."
  [repo]
  (->> repo
       (.tagList)
       (.call)
       (map #(->> % .getName (re-matches #"refs/tags/(.*)") second))))


(defn- last-commit-info
  [root]
  (p/with-repo root
    (q/commit-info repo (first (p/git-log repo)))))


(defn- last-tag
  [root]
  (p/with-repo root
    (-> repo git-tag-list last)))


(defn- compose-git-info
  [commit tag datetime-format]
  (format "%s#%s %s" (str tag) (:id commit) (unparse (formatter datetime-format)
                                                     (-> "." last-commit-info :time c/from-date))))


(defn- as-path
  [file-path]
  (-> file-path io/as-file io/as-url .toURI Paths/get))


(defn- bake-into-jar
  [config git-info]
  (let [fs (FileSystems/newFileSystem (as-path (:jar-path config)) nil)
        target-path (.getPath fs (:project-dir config) (into-array String [(:config-name config)]))
        parsed (-> target-path .toUri slurp (string/replace (:placeholder config) git-info))]
    (Files/copy (-> parsed .getBytes io/input-stream)
                target-path
                (into-array StandardCopyOption [StandardCopyOption/REPLACE_EXISTING]))
    (.close fs)))


(defn- compose-config
  [project]
  (let [jar-name (->> [(-> project :bake-git-info :jar-name) (:uberjar-name project) "application.jar"]
                      (remove string/blank?)
                      first)]
    {:datetime-format (->> [(-> project :bake-git-info :datetime-format) "yyyy.MM.dd HH:mm:ss"]
                           (remove string/blank?)
                           first)
     :jar-path (path-join [(:root project) "target" jar-name])
     :config-name (->> [(-> project :bake-git-info :config-name) "config.edn"]
                       (remove string/blank?)
                       first)
     :project-dir (->> [(-> project :bake-git-info :project-dir) (str (:name project))]
                       (remove string/blank?)
                       first)
     :placeholder (->> [(-> project :bake-git-info :placeholder) "BAKE_GIT_INFO_PLACEHOLDER"]
                       (remove string/blank?)
                       first)
     :verbose? (->> [(-> project :bake-git-info :verbose?) true]
                    (remove nil?)
                    first)}))


(defn bake-git-info
  [project & args]
  (let [config (compose-config project)
        git-info (compose-git-info (last-commit-info ".") (last-tag ".") (:datetime-format config))]
    (if (some? (:project-dir config))
      (do
        (bake-into-jar config git-info)
        (when (:verbose? config)
          (info (format "bake-git-info> Bumped: %s" git-info))))
      (warn "No project-dir found"))))
