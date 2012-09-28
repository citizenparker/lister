(ns lister.lists
  (require [accession.core :as redis]))

(def *name-key* "name")

(def *default-name* "My Cool List")

(defn list-name [list] (or (list *name-key*) *default-name*))

(defn list-items [list] (dissoc list *name-key*))

(def new-list {})

(defn generate-key [] (clojure.string/replace (str (java.util.UUID/randomUUID)) "-" ""))

(def conn (redis/connection-map {}))

(defn get-list [list-key]
  (let [saved-list (redis/with-connection conn (redis/hgetall list-key))]
    (if (empty? saved-list)
      new-list
      (apply array-map saved-list))))

(defn upsert-list-item [list-key item-key item]
  (redis/with-connection conn (redis/hset list-key item-key item)))

(defn remove-from-list [list-key item-key]
  (redis/with-connection conn (redis/hdel list-key item-key)))

(defn clear-list [list-key]
  (let [item-keys (remove #{*name-key*} (redis/with-connection conn (redis/hkeys list-key)))]
    (redis/with-connection conn (apply redis/hdel (flatten [list-key item-keys])))))

(defn add-to-list [list-key item]
  (upsert-list-item list-key (generate-key) item))

(defn rename-list [list-key new-name]
  (upsert-list-item list-key *name-key* new-name))
