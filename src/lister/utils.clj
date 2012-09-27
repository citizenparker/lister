(ns lister.utils)

(defn new-uuid [] (clojure.string/replace (str (java.util.UUID/randomUUID)) "-" ""))
