(ns fy.flickr-photo-feed-test
  (:require [fy.flickr-photo-feed :as f]
            [clojure.test :refer [deftest testing is]]))

(deftest parse-xml-string-test
  (testing "Parsing an XML string into a clj map"
    (is (= (f/parse-xml-string "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<note>\n  <to>Tove</to>\n  <from>Jani</from>\n  <heading>Reminder</heading>\n  <body>Don't forget me this weekend!</body>\n</note> \n")
           {:tag :note, :attrs nil,
            :content [{:tag :to, :attrs nil, :content ["Tove"]}
                      {:tag :from, :attrs nil, :content ["Jani"]}
                      {:tag :heading, :attrs nil, :content ["Reminder"]}
                      {:tag :body, :attrs nil, :content ["Don't forget me this weekend!"]}]}))))