(ns wechat-clj.util
  (:require [cognitect.transit :as t]
            [clojure.string :as str]))

(defn ^:export is-weixin []
  (let [ua (-> js/navigator
             .-userAgent
             .toLowerCase)]
    (not= (.indexOf ua "micromessenger") -1)))

(def json-reader (t/reader :json))

(defn json-string-to-clj [json-stri]
  (t/read json-reader
    (-> json-stri
      (str/replace "&quot;" "\""))))

(defn get-params
  "url的参数解析为hash"
  []
  (->>
    (clojure.string/split
      (.-search js/location) #"\?|\&")
    (filter not-empty)
    (map #(clojure.string/split % #"="))
    (into {})))

(comment
  ;; cljs生成的html页面class转为行内样式的帮助函数,在Emacs开发小程序页面中使用
  (find-class-name-style "w-1000") ;; => ".w-100 { width: 100%; }"
  (get-class-names-styles "flex flex-row h3 pa3 f4 w-100x")
  ;; => ("display: flex;" "flex-direction: row;" "height: 4rem;" "padding: 1rem;" "font-size: 1.25rem;")
  )
(defn find-class-name-style
  "查找单个类名对应的样式是什么"
  [query]
  (let [rules
        (array-seq
          (.-cssRules (first (array-seq js/document.styleSheets))))]
    (try
      (.-cssText
        (first
          (filter (fn [rule]
                    (= (str "." query)  (.-selectorText rule)))
            rules)))
      (catch :default e
        (js/console.log (str query "类名没有找到style: " e))
        ""))))

(defn get-class-names-styles
  "查找多个类名对应的样式是什么"
  [class-stri]
  (->>
    (clojure.string/split class-stri #" ")
    (map (fn [css-name]
           (clojure.string/replace
             (find-class-name-style css-name)
             #".(.*) \{ (.*) \}"
             "$2")))
    (clojure.string/join "")))
