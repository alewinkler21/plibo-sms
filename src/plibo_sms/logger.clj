(ns plibo-sms.logger
  (:require [clojure.tools.logging :as log]))

(def TAG (java.util.UUID/randomUUID))

(defn format-message [message]
  (format "[%s]%s" TAG message))

(defn debug [message]
  (log/debug (format-message message)))

(defn info [message]
  (log/info (format-message message)))

(defn warning [message]
  (log/warn (format-message message)))

(defn error [message]
  (log/error (format-message message)))