(ns fy.utils
  (:require [clj-compress.core :as c]
            [clojure.java.io :as io])
  (:import [java.io ByteArrayOutputStream]))

(defn write-to-zip-file
  "Archives a bunch of files into a single zip file
  NOTE: Created archive will be present in the `/tmp` folder"
  [zip-file files]
  (c/create-archive zip-file files "/tmp" "bzip2")
  (str "/tmp/" zip-file ".tar.bz2"))

(defn write-to-file
  "Writes an input stream `i` to `file`
  and returns the `file` path.
  NOTE: Created file will be present in the `/tmp` folder"
  [i file]
  (let [full-path-file (str "/tmp/" file)]
    (with-open [out (io/output-stream full-path-file)]
      (io/copy i out))
    full-path-file))