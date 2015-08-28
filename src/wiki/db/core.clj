(ns wiki.db.core
  (:require [taoensso.timbre :as timbre]
            [environ.core :refer [env]]))

(defonce data-error-message {:message "Unable to process Data"
                             :error_code 1
                             :error_message "Data Error"})