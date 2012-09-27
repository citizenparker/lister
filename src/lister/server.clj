(ns lister.server
  (:require [lister.utils :as utils]
            [compojure.core :as comp]
            [compojure.route :as route]
            [ring.adapter.jetty :as ring-jetty]
            [net.cgrand.enlive-html :as html]))

(html/deftemplate index "html/index.html" [context])

(defn new-list [c] (ring.util.response/redirect (str "/lists/" (utils/new-uuid))))

(comp/defroutes all-routes
  (comp/GET "/" [] index)
  (comp/GET "/new" [] new-list)
  (route/files "/")
  (route/not-found "<h1>Page not found!</h1>"))

(defn -main []
  (defonce server (ring-jetty/run-jetty (var all-routes) {:port 6464 :join? false})))

(defn stop []
  (.stop server))

(+ 5 6)
