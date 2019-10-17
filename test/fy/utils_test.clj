(ns fy.utils-test
  (:require [fy.utils :as u]
            [clojure.java.io :as io]
            [clojure.string :refer [trim-newline]]
            [clojure.test :refer [deftest testing is]])
  (:import [java.util.zip ZipInputStream]))

(defn string->stream
  ([s] (string->stream s "UTF-8"))
  ([s encoding]
   (-> s
       (.getBytes encoding)
       (java.io.ByteArrayInputStream.))))

(deftest write-to-file-test
  (testing "Writing an input steram to a file"
    (u/write-to-file (string->stream "This is a test!") "test.test")
    (is (= (trim-newline (slurp "/tmp/test.test"))
           "This is a test!"))
    (io/delete-file "/tmp/test.test")))

(deftest write-to-zip-file-test
  (testing "Writing an input steram to a zip file"
    (spit "/tmp/test.test" "This is a test!")
    (u/write-to-zip-file "test.test" ["/tmp/test.test"])
    (let [zis (ZipInputStream. (io/input-stream "/tmp/test.test.zip"))
          next-entry (.getNextEntry zis)
          next-entry-size (count (.getBytes (trim-newline (slurp "/tmp/test.test"))))
          next-entry-data (byte-array next-entry-size)]
      ;; Copying data into the array
      (.read zis next-entry-data 0 next-entry-size)
      (is (= (.getName next-entry) "test.test"))
      (is (= (trim-newline (String. next-entry-data "UTF-8"))
             (trim-newline (slurp "/tmp/test.test")))))
    (io/delete-file "/tmp/test.test")
    (io/delete-file "/tmp/test.test.zip")))