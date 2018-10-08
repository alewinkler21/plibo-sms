(ns plibo-sms.core-test
  (:require [clojure.test :refer :all]
            [plibo-sms.client :refer :all]))

(def client (create "YOUR AUTH ID" "YOUR AUTH TOKEN"))
(def source-number "+59899112233")
(def destiny-number "+59899112234")

(deftest test-send-sms!
  (testing "Testing send-sms!"
    (let [{:keys [status]} (send-sms! client source-number destiny-number "test text")]
      (is (successful-response? status)))))

(deftest test-check-sms-status
  (testing "Testing check-sms-status"
    (let [messages (atom '())]
      (check-sms-status client
                        :fn-process-page (fn [msgs]
                                           (doseq [msg msgs]
                                             (swap! messages #(conj % msg)))))
      (is (> (count @messages) 0)))))