(ns wiki.db.core
  (:require [taoensso.timbre :as timbre]
            [environ.core :refer [env]]
            [clojure.java.jdbc :as j]))

(defonce data-error-message {:message "Unable to process Data"
                             :error_code 1
                             :error_message "Data Error"})
(def mysql-db {:subprotocol "mysql"
               :subname "//127.0.0.1:3306/wiki"
               :user "root"
               :password "root"})

(defn insert [title content creation-time modified-time]
  (j/insert! mysql-db :articles
    {:title title
     :content content
     :creation_time creation-time
     :modified_time modified-time
     :edits 0}))

(defn get-all-articles []
  (j/query mysql-db ["select * from articles"]))

(defn get-edits [article-id]
  (first (j/query mysql-db ["select edits from articles where id = ?" article-id] :row-fn :edits)))

(defn get-title [article-id]
  (first (j/query mysql-db ["select title from articles where id = ?" article-id] :row-fn :title)))

(defn get-content [article-id]
  (first (j/query mysql-db ["select content from articles where id = ?" article-id]) :row-fn :content))

(defn update-edits [article-id edit-count]
  (j/update! mysql-db :articles {:edits edit-count} ["id = ?" article-id]))

(defn increment-edits [article-id]
  (j/with-db-transaction [t-con mysql-db]
    (let [edits (get-edits article-id)]
      (update-edits article-id (+ edits 1)))
    ))


