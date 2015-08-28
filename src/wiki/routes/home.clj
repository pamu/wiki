(ns wiki.routes.home
  (:require [wiki.layout :as layout]
            [compojure.core :refer [defroutes GET POST OPTIONS]]
            [ring.util.http-response :refer [ok]]
            [wiki.db.core :as db]
            [wiki.processor.core :as processor]
            [clojure.java.io :as io]))

(defn home-page []
  (layout/render
    "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn about-page []
  (layout/render "about.html"))

(defroutes home-routes
  (GET "/" [] (home-page))
  (OPTIONS "/" []
    {:status 200
     :headers {"Access-Control-Allow-Methods" "OPTIONS, GET"}})

  (GET "/about" [] (about-page))

  (GET "/create" req
    (processor/process-create req))
  (OPTIONS "/create" []
    {:status 200
     :headers {"Access-Control-Allow-Methods" "OPTIONS, GET"}})

  (GET "/update-article" req
    (processor/process-update-article req))
  (OPTIONS "/update-article" []
    {:status 200
     :headers {"Access-Control-Allow-Methods" "OPTIONS, GET"}})

  (GET "/get-articles" req
    (processor/process-get-articles req))
  (OPTIONS "/get-articles" []
    {:status 200
     :headers {"Access-Control-Allow-Methods" "OPTIONS, GET"}})

;  (POST "/login" req
;    (processor/process-login req))
;  (OPTIONS "/login" []
;    {:status 200
;     :headers {"Access-Control-Allow-Methods" "POST, OPTIONS"}})
  )