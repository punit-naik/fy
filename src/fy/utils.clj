(ns fy.utils
  (:require [me.raynes.fs.compression :as zip-utils]
            [clojure.java.io :as io]))

(defn write-to-zip-file
  "Archives a bunch of files into a single zip file
  `zip-file` -> Name of the zip file archive
  `files`    -> List of string paths of files to be compressed
  NOTE: Created archive will be present in the `/tmp` folder"
  [zip-file files]
  (let [zip-file-path (str "/tmp/" zip-file ".tar.bz2")]
    (zip-utils/zip-files zip-file-path files)
    zip-file-path))

(defn write-to-file
  "Writes an input stream `i` to `file`
  and returns the `file` path.
  NOTE: Created file will be present in the `/tmp` folder"
  [i file]
  (let [full-path-file (str "/tmp/" file)]
    (with-open [out (io/output-stream full-path-file)]
      (io/copy i out))
    full-path-file))