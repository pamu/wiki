(ns wiki.db.core
  (:require [taoensso.timbre :as timbre]
            [environ.core :refer [env]]
            [clojure.java.jdbc :as j]))

(defonce data-error-message {:message "Unable to process Data"
                             :error_code 1
                             :error_message "Data Error"})
(def mysql-db {:classname "com.mysql.jdbc.Driver"
               :subprotocol "mysql"
               :subname "//127.0.0.1:3306/wiki"
               :user "root"
               :password "root"})


(defn current-time []
  (let [date (new java.util.Date)]
    (let [sdf (new java.text.SimpleDateFormat "yyyyMMddHHmmss")]
      (.format sdf (.getTime date)))))

(defn insert [title content]
  (j/insert! mysql-db :articles
    {:title title
     :content content
     :creation_time (current-time)
     :modified_time (current-time)
     :edits 0}))

(defn get-article [article-id]
  (j/query mysql-db ["select * from articles where id = ?" article-id]))

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

(defn update-content [article-id content]
  (j/update! mysql-db :articles {:content content} ["id = ?" article-id]))

(defn update-modified-time [article-id]
  (j/update! mysql-db :articles {:modified_time (current-time)} ["id = ?" article-id]))

(defn update-article [article-id content]
  (j/with-db-transaction [t-con mysql-db]
    (do
      (update-content article-id content)
      (update-modified-time article-id)
      (increment-edits article-id))))

(defn parse-int [str]
  (Integer. str))

(defn get-articles [page-size page-number]
  (j/query mysql-db ["select * from articles order by id asc limit ?, ?"
                     (* (parse-int page-size) (- (parse-int page-number) 1))
                     (parse-int page-size)]))

(defn remove-article [article-id]
  (j/delete! mysql-db :articles ["id=?" article-id]))








