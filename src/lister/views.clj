(ns lister.views
  (:require [lister.lists :as lists]
            [net.cgrand.enlive-html :as e]
            [ring.util.response :as response]))

(e/deftemplate base "html/base.html" [{:keys [title content]}]
  [:title] (e/content (str "Lister! - " title))
  [:#content] (e/content (content)))

(e/defsnippet list-item-model "html/listview.html" [:#items-form :> [[:div (e/nth-of-type 1)]]] [list-key [item-key item]]
                   [:label] (e/do->
                              (e/set-attr :for item-key)
                              (e/content item))
                   [:button] (e/do->
                               (e/set-attr :name item-key
                                                :id item-key
                                                :formaction (str "/list/" list-key "/done/" item-key))))

(defn show-list [list-key]
  (let [list (lists/get-list list-key)
        list-name (lists/list-name list)
        list-items (lists/list-items list)]
    (base {:title list-name
           :content (e/snippet "html/listview.html" [e/root] []
                                    [:#rename-form] (e/set-attr :action (str "/list/" list-key "/rename"))
                                    [:#rename-form :legend] (e/content list-name)
                                    [:#name] (e/set-attr :value list-name)
                                    [:#clear-form] (e/set-attr :action (str "/list/" list-key "/clear"))
                                    [:#items-form] (e/content (map #(list-item-model list-key %) list-items))
                                    [:#new-item-form] (e/set-attr :action (str "/list/" list-key "/new-item")))})))

(defn index []
  (base {:title "Welcome"
         :content (e/snippet "html/index.html" [e/root] [])}))

(defn new-list [] (response/redirect (str "/list/" (lists/generate-key))))

(defn new-item [list-key item]
  (lists/add-to-list list-key item)
  (response/redirect (str "/list/" list-key)))

(defn rename-list [list-key new-name]
  (lists/rename-list list-key new-name)
  (response/redirect (str "/list/" list-key)))

(defn clear-list [list-key]
  (lists/clear-list list-key)
  (response/redirect (str "/list/" list-key)))

(defn item-done [list-key item-key]
  (lists/remove-from-list list-key item-key)
  (response/redirect (str "/list/" list-key)))
