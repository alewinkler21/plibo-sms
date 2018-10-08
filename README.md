# plibo-sms

A Clojure library designed to send SMS messages through [plibo SMS API Platfotm](https://www.plivo.com/sms/).


## Installation

To install, add the following dependency to your project.clj file:

```
[plibo-sms "0.1.0"]
```

## Usage

To get started with using hawk, require hawk.core in your project:

```
(ns plibosms.sample
       (:require [plibo-sms.client :as plibo]))
```

#### Create your client

```
(def client (plibo/create "YOUR AUTH ID" "YOUR AUTH TOKEN"))
```

#### Send SMS

```
(plibo/send-sms! client source-number destiny-number text)
```

**Note**

*destiny-number*: can be a number (as a string) or a list of numbers (strings):

"+59899112233" or ["+59899112233" "+59899112234"]

#### Check SMS status

```
(plibo/check-sms-status client
                        :since "2018-10-07 11:26:42"
                        :limit 20
                        :fn-process-page (fn [msgs]
                                           (doseq [msg msgs]
                                             (println msg))))
```

**Parameters**

*Limit*: the count of items in each page when paginating (20 is the max number and the default value).

*Since*: the date since when you retrieve the data (default is last hour).

*fn-process-page*: a custom function you provide for processing each page. Normally you will use this function to update and persist the status for each sms sent.

## License

Copyright © 2018 Alejandro Winkler

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
