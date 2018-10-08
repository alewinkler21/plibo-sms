(ns plibo-sms.date-utils
  (:require [clj-time.core :as t]
            [clj-time.format :as f]
            [plibo-sms.logger :as log]))

(defn parse-date
  [str-date]
  (if (= (count str-date) 0)
    str-date
    (try
      (f/parse (f/formatter "yyyy-MM-dd HH:mm:ss.SSSSSSZ") str-date)
      (catch Exception e
        (log/error (.getMessage e))
        nil))))

(defn last-hour
  []
  (f/unparse (f/formatter :mysql) (t/plus (t/now) (t/hours -1))))