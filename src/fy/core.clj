(ns fy.core
  (:require [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]
            [reitit.ring :as ring]
            [reitit.coercion.spec]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.parameters :as parameters]
            [muuntaja.core :as m]
            [clojure.spec.alpha :as s]
            [fy.flickr-photo-feed :refer [download-as-zip]]
            [ring.adapter.jetty :as jetty]
            [clojure.java.io :as io]))

;; Spec

(s/def ::width pos-int?)
(s/def ::height pos-int?)
(s/def ::limit pos-int?)
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
               :handler (fn [{{width "width", height "height", limit "limit"} :query-params}]
                          (let [limit (when limit (Integer/parseInt limit))
                                width (when width (Integer/parseInt width))
                                height (when height (Integer/parseInt height))
                                f (io/file (download-as-zip limit [width height]))]
                            {:status 200 :headers {"Content-Type" "application/zip"
                                                   "Content-Disposition" (str "attachment; filename=" (.getName f))}
                             :body f}))}}]]])

(def router
  (ring/router routes
               {:data {:coercion reitit.coercion.spec/coercion
                       :muuntaja m/instance
                       :middleware [;; swagger feature
                                    swagger/swagger-feature
                                    ;; query-params & form-params
                                    parameters/parameters-middleware
                                    ;; content-negotiation
                                    muuntaja/format-negotiate-middleware
                                    ;; encoding response body
                                    muuntaja/format-response-middleware
                                    ;; exception handling
                                    exception/exception-middleware
                                    ;; decoding request body
                                    muuntaja/format-request-middleware
                                    ;; coercing response bodys
                                    coercion/coerce-response-middleware
                                    ;; coercing request parameters
                                    coercion/coerce-request-middleware]}}))

(def app
  (ring/ring-handler router
                     (ring/routes
                       (swagger-ui/create-swagger-ui-handler {:path "/swagger-ui"}))))

(defn start
  [port]
  (jetty/run-jetty #'app {:port port :join? false})
  (println "Server started running on port" port))

(defn -main
  [& args]
  (start (if (seq args)
           (Integer/parseInt (first args)) 3000)))