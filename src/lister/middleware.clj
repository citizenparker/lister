(ns lister.middleware
  "Middleware in support of development tasks")

(defn wrap-reload
  "Forces the reloading of each namespace in reloadables on each request. Similar to ring.middleware.reload prior to f39e24da7"
  [app reloadables]
  (fn [req]
    (doseq [ns-sym reloadables]
      (require ns-sym :reload))
    (app req)))
