(ns test2.core
  (:gen-class)
  (:require [compojure.core :as c]
            [compojure.route :as route]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.defaults :as ring-defaults]
            [hiccup.core :as h]))

(defonce server (atom nil))

(defn home-view [count]
  [:html
   [:body
    [:h1 "Welcome home!"]
    [:ul
     (for [i (range count)]
       [:li i])]]])

(defn routes []
  (c/routes
    (c/GET "/" [count]
      {:status  200
       :headers {"Content-Type" "text/html"}
       :body    (str (h/html (home-view (Integer. (or count 0)))))})
    (c/GET "/:foo" [foo]
      {:status 200
       :body (str "you asked for " foo)})))

(defn handler [req]
  ((routes) req))

(defn stop-jetty! []
  (.stop @server)
  (reset! server nil))

(defn start-jetty! []
  (reset!
    server
    (jetty/run-jetty (ring-defaults/wrap-defaults
                       #'handler
                       ring-defaults/site-defaults)
                     {:join false :port 3001})))

(defn -main [& args]
  (start-jetty!))
