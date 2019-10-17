(ns fy.flickr-photo-feed
  (:require [clj-http.client :as client]
            [image-resizer.core :as img-core]
            [image-resizer.format :as img-format]
            [image-resizer.util :as img-utils]
            [clojure.xml :as xml]
            [clojure.string :as clj-str]
            [fy.utils :as utils])
  (:import [java.util.zip ZipEntry ZipOutputStream]))

(defn take-limited
  "Takes a limited amount of entries from the feed"
  [feed limit]
  (if limit (take limit feed) feed))

(defn parse-xml-string
  "Parses an xml"
  [xml-str]
  (->> xml-str
       .getBytes
       java.io.ByteArrayInputStream.
       xml/parse))

(defn buffered-image->input-stream
  "Converts an image of type `java.awt.image.BufferedImage` to `java.io.ByteArrayInputStream`"
  [i]
  (img-format/as-stream i "jpg"))

(defn extract-named-links
  "Extracts titles and links of public feed photos from the feed data
  as a k-v pair like [`title` `link`]"
  [feed]
  (map
   (fn [entry]
     [(get-in entry [:content 0 :content 0])
      (-> (get-in entry [:content 7 :content 0])
          (clojure.string/split #"img src=\"") second
          (clojure.string/split #"\"") first)]) feed))

(defn fetch-feed-xml
  "Fetches flickr public photo feed data in xml format"
  []
  (->> (client/get "https://www.flickr.com/services/feeds/photos_public.gne") :body))

(defn fetch-photo-links
  "Fetches named links from the flickr public photo feed"
  [limit]
  (-> (fetch-feed-xml)
      parse-xml-string :content
      (nthrest 8) ;; Because the actual feed data starts from the 8th position in the vector
      (take-limited limit)
      extract-named-links))

(defn resize-image
  "Reads image data from the URL and resizes them if possible.
  Returns a `BufferedImage`"
  [url width height]
  (if (and (pos-int? width)
           (pos-int? height))
    (img-core/force-resize (img-utils/buffered-image (java.net.URL. url)) width height)
    (img-utils/buffered-image (java.net.URL. url))))

(defn download-as-zip
  "Fetches images from the Flickr public photo feed URL, resizes and stores them
  and compresses all of them in a single archive"
  [limit [width height]]
  (->> (fetch-photo-links limit)
       (pmap
        (fn [[_ url]]
          (utils/write-to-file (buffered-image->input-stream (resize-image url width height))
                               (str (java.util.UUID/randomUUID) ".jpg"))))
       (utils/write-to-zip-file (str (java.util.UUID/randomUUID)))))