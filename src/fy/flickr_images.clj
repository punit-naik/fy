
(ns fy.flickr-images)

(defn fetch
  "Fetches flickr images from the URL: https://www.flickr.com/services/feeds/photos_public.gne"
  []
  (constantly {:status 200 :body "It works!"}))