(ns lister.server
  (:require [lister.views :as views]
            [lister.middleware :as middleware]
            [compojure.core :as compojure]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.adapter.jetty :as jetty]))

(compojure/defroutes all-routes
                     (compojure/GET "/" [] (views/index))
                     (compojure/GET "/new" [] (views/new-list))
                     (compojure/GET "/list/:list-key" [list-key] (views/show-list list-key))
                     (compojure/POST "/list/:list-key/new-item" [list-key new-item] (views/new-item list-key new-item))
                     (compojure/POST "/list/:list-key/rename" [list-key name] (views/rename-list list-key name))
                     (compojure/POST "/list/:list-key/clear" [list-key] (views/clear-list list-key))
                     (compojure/POST "/list/:list-key/done/:item-key" [list-key item-key] (views/item-done list-key item-key))
                     (route/files "/")
                     (route/not-found "<h1>Page not found!</h1>"))

(def app (handler/site (middleware/wrap-reload all-routes ['lister.views])))

(defn -main []
  (defonce server (ring.adapter.jetty/run-jetty (var app) {:port 6464 :join? false})))
