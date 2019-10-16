(ns fy.core
  (:require [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]
            [reitit.ring :as ring]
            [reitit.coercion.spec]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.parameters :as parameters]
            [muuntaja.core :as m]
            [clojure.spec.alpha :as s]
            [fy.flickr-images :refer [fetch]]
            [ring.adapter.jetty :as jetty]))

;; Spec

(s/def ::width int?)
(s/def ::height int?)
(s/def ::limit int?)
(s/def ::fetch-feed-params (s/keys :opt-un [::width ::height ::limit]))

;;

(def routes
  [["/swagger.json"
    {:get {:no-doc true
           :swagger {:info {:title "Fy Programming Challenge"}}
           :handler (swagger/create-swagger-handler)}}]
   ["/" {:get {:handler (constantly {:status 200 :body "Welcome!"})}}]
   ["/feed"
    {:swagger {:tags ["Flickr Public Photo Feed"]}}
    ["" {:get {:summary "Fetches Flickr public photo feed"
                :parameters {:query ::fetch-feed-params}
                :responses {200 {:body string?}}
                :handler (fetch)}}]]])

(def router
  (ring/router routes
               {:data {:coercion reitit.coercion.spec/coercion
                       :muuntaja m/instance
                       :middleware [parameters/parameters-middleware
                                    swagger/swagger-feature
                                    muuntaja/format-middleware
                                    coercion/coerce-request-middleware]}}))

(def app
  (ring/ring-handler router
                     (ring/routes
                       (swagger-ui/create-swagger-ui-handler {:path "/swagger-ui"}))))

(defn start
  [port]
  (jetty/run-jetty #'app {:port port :join? false})
  (println "Server started running on port" port))

(comment
  (start 3000))