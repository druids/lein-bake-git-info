lein-bake-git-info
=================

A Leiningen plugin that bakes a git information into a JAR file. This plugin takes a GIT information and updates a JAR
 within the information. The main purpose of this plugin is to "hard-code" a GIT information into a JAR file,
 thus you will ever know from which revision is the JAR created. The plugin is usually called after a JAR creation
 (e.g. `lein uberjar`). The plugin takes a GIT info (currently a revision and tag) from a host repository, composes
 a git info message, and replaces a placeholder (in a file) in a target JAR.

[![Dependencies Status](https://jarkeeper.com/druids/lein-bake-git-info/status.png)](https://jarkeeper.com/druids/lein-bake-git-info)
[![License](https://img.shields.io/badge/MIT-Clause-blue.svg)](https://opensource.org/licenses/MIT)


Leiningen/Boot
--------------
Add following line into a project's `:plugins`

```clojure
[lein-bake-git-info "0.1.0"]
```

Usage
-----

When a JAR file is created just call `lein bake-git-info`. You should see an info message in a console like
 `bake-git-info> Bumped: 0.1.0#f55389107489994f674e448d56659f13d32d833b 2018.04.09 17:41:27`
 in case that `:verbose?` is not set to `false`.

Documentation
-------------

By default the plugin replaces `BAKE_GIT_INFO_PLACEHOLDER` by a git info message with datetime format
 `yyyy.MM.dd HH:mm:ss` on path `<project-name>/config.edn` in `target/application.jar`.


Options:

* `:placeholder` a string that will be replaced by a git info message, default `"BAKE_GIT_INFO_PLACEHOLDER"`
* `:datetime-format` a string of a format, default is `"yyyy.MM.dd HH:mm:ss"`
* `:config-name` a name of a file that contains the placeholder string, default `"config.edn"`
* `:verbose?` when true, it displays a message when a message is baked in a JAR, default `true`
* `:project-dir` a folder name where the config exists, default a project name
* `:jar-name` a name of a JAR, default a value from `:uberjar-name` or `"application.jar"`

All options above can be set in `project.clj` under `:bake-git-info` key. E.g.:

```clojure
(defproject myapp "0.1.0"
  :bake-git-info {:placeholder "CUSTOMER_PLACEHOLDER"})
```
