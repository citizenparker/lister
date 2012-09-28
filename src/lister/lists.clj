(ns lister.lists
  (require [accession.core :as redis]))

(def *name-key* "name")

(def *default-name* "My Cool List")

(defn list-name [list] (or (list *name-key*) *default-name*))

(defn list-items [list] (dissoc list *name-key*))

(def new-list {})

(defn generate-key [] (clojure.string/replace (str (java.util.UUID/randomUUID)) "-" ""))

(def conn
  (if-let [uri-str (System/getenv "REDISTOGO_URL")]
    (let [uri (java.net.URI. uri-str)]
      (redis/connection-map {:host (.getHost uri)
                             :port (.getPort uri)
                             :password (last (clojure.string/split (.getUserInfo uri) #":"))}))
    (redis/connection-map {})))

(defn exec-command
  "This will attempt to precede a command with auth. This happens even when no password is specified, but will silently fail in that case."
  [rcommand]
  (let [results (redis/with-connection conn (redis/auth (conn :password)) rcommand)]
    (second results)))

(defn get-list [list-key]
  (let [saved-list (exec-command (redis/hgetall list-key))]
    (if (empty? saved-list)
      new-list
      (apply array-map saved-list))))

(defn upsert-list-item [list-key item-key item]
  (exec-command (redis/hset list-key item-key item)))

(defn remove-from-list [list-key item-key]
  (exec-command (redis/hdel list-key item-key)))

(defn clear-list [list-key]
  (let [item-keys (remove #{*name-key*} (exec-command (redis/hkeys list-key)))]
    (exec-command (apply redis/hdel (flatten [list-key item-keys])))))

(defn add-to-list [list-key item]
  (upsert-list-item list-key (generate-key) item))

(defn rename-list [list-key new-name]
  (upsert-list-item list-key *name-key* new-name))
