(ns plibo-sms.client
  (:require [clj-http.client :as http-client]
            [cheshire.core :refer [generate-string parse-string]]
            [plibo-sms.logger :as log]
            [plibo-sms.date-utils :refer [last-hour]]
            [clojure.string :as string]))

(def base-url "https://api.plivo.com/v1")

(defrecord Client [auth-id auth-token clj-http-options])

(defn- request
  ([client method url]
   (request client method url {}))
  ([client method url params]
   (let [params (merge {:headers      {"User-Agent" "plibo-sms/0.1.0"}
                        :as           :json
                        :content-type :json
                        :basic-auth   ((juxt :auth-id :auth-token) client)
                        :method       method
                        :url          url}
                       (if (= method :get)
                         {:query-params params}
                         {:body (generate-string params)})
                       (:clj-http-options client))]
     (try
       (let [{:keys [status body]} (http-client/request params)]
         {:status status :body body})
       (catch Exception e
         (let [{:keys [status body]} (ex-data e)
               body (if (= status 401)
                      body
                      (merge (-> (:body params)
                                 (parse-string true)
                                 (dissoc :text))
                             (parse-string body true)))]
           (log/error (format "Status: %s - %s" status body))
           {:status status :body body}))))))

(defn successful-response?
  [status]
  (some #(= status %) '[200 201 202 204]))

(defn- sms-url
  [client]
  (format "%s/Account/%s/Message/" base-url (:auth-id client)))

(defn create
  ([api-key auth-token]
   (create api-key auth-token {}))
  ([api-key auth-token clj-http-options]
   (->Client api-key auth-token clj-http-options)))

(defn send-sms!
  [client source destiny text]
  (let [destiny (if (coll? destiny)
                  (string/join "<" destiny)
                  destiny)]
    (request client :post (sms-url client) {:src source :dst destiny :text text})))

(defn check-sms-status
  [client & {:keys [limit since fn-process-page] :or {limit 20 since (last-hour)}}]
  (loop [offset 0 continue true]
    (when continue
      (let [params (merge {:limit limit :offset offset}
                          (when since {:message_time__gt since}))
            {:keys [status body]} (request client :get (sms-url client) params)
            objects (if (successful-response? status) (:objects body) [])]
        (when (some? fn-process-page)
          (fn-process-page objects))
        (recur (+ offset limit) (>= (count objects) limit))))))