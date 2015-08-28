(ns wiki.processor.core
  (:require [wiki.db.core :as db]
            [taoensso.timbre :as timbre]
            [clojure.stacktrace :as clj-stacktrace]
            [clojure.string :as string]
            [clojure.walk :as walk]
            [noir.response :refer [json]]
            [noir.util [crypt :as crypt]]
            [cheshire.core :refer [parse-string]]
            [clojure.core :refer :all])
  (:import javax.xml.bind.DatatypeConverter))

(defonce auth-fail-message {:message "Authentication Fail"
                   :error_code 401
                   :error_message "Authentication Fail"})
(defonce record-success-message {:message "Inspection Details Recorded"
                               :error_code 0
                               :error_message nil})
(defonce record-success-reschedule-message {:message "Reschedule Processed"
                                            :error_code 0
                                            :error_message nil})
(defonce login-success-message {:message "Hi"
                               :error_code 0
                               :error_message nil})

(defn keywordize-remove-beginning-$-keys [m]
  (let [f (fn [[k v]]
            (if (string? k)
              (if-not (.startsWith k "$") [(keyword k) v])
              [k v]))]
    (walk/postwalk (fn [x]
                     (if (map? x) (into {} (map f x)) x)) m)))

(defn- sanitize [data]
  (if (string? data)
    (if (re-matches #"\d+" data)
      (Integer/parseInt data)
      (string/trim data))
    data))

(defn- sanitize-data [data]
  (try
    (keywordize-remove-beginning-$-keys data)
;    (apply merge (map #(hash-map (key %) (sanitize (-> % val keywordize-remove-beginning-$-keys))) data))
    (catch Exception e
      (timbre/error "Data Format Exception" e)
      {:message "Data Format Exception"
       :error_code 1
       :error_message (.getMessage e)})))

;(defn parse-creds [auth]
;  (let [basic-creds (second (re-matches #"\QBasic\E\s+(.*)" auth))]
;    (->> (String. (DatatypeConverter/parseBase64Binary basic-creds) "UTF-8")
;      (re-matches #"(.*):(.*)")
;      rest)))
;
;(defn check-auth [req]
;  (try
;    (let [[inspector pass] (vec (parse-creds (get-in req [:headers "authorization"])))
;          account (db/get-inspector inspector)
;          _ (timbre/info inspector pass)
;          ]
;      account)
;    (catch Exception e
;      (timbre/error "Authorization header missing" e))))

(defn process-create [req]
  (try
    (let [result (db/insert (get (req :params) :title) (get (req :params) :content))]
      (json {:message "success" :generated_key (:generated_key (first result))}))
    (catch Exception e
      (timbre/error "Database Exception" e))))

(defn process-update-article [req]
  (try
    (let [result (db/update-article (get (req :params) :id) (get (req :params) :content))]
      (json {:message "success" :effected_rows (first result)}))
    (catch Exception e
      (timbre/error "Database Exception" e))))

