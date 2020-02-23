(ns screener.models.core
  (:require [clojure.spec.alpha :as s]
            [java-time :as jtime]
            [clojure.string :as st]
            [screener.models.num :refer :all]
            [screener.models.tag :refer :all]
            [screener.models.pre :refer :all]
            [screener.models.sub :refer :all])
  (:import [screener.models.num Num]
           [screener.models.tag Tag]
           [screener.models.pre Pre]
           [screener.models.sub Sub]))

(def records-map {:sub "sub"
                  :tag "tag"
                  :num "num"
                  :pre "pre"})

(def tables {:sub :submissions
             :tag :tags
             :num :numbers
             :pre :presentations})

(defmacro create-record
  "Returns expression that will create a record from specified record-type and src-map"
  [record-type src-map]
  `((symbol (str (~records-map ~record-type))) ~src-map))

;; Field validations

(s/def ::adsh (s/and string? #(<= (count %) 20)))

(s/def ::tag (s/and string? #(<= (count %) 256)))

(s/def ::version (s/and string? #(<= (count %) 20)))

(s/def ::cik #(number? (read-string %)))

(s/def ::name (s/and string? #(<= (count %) 150)))

(s/def ::sic #(number? (read-string %)))

(s/def ::countryba (s/and string? #(<= (count %) 2)))

(s/def ::stprba (s/and string? #(<= (count %) 2)))

(s/def ::cityba (s/and string? #(<= (count %) 30)))

(s/def ::zipba (s/and string? #(<= (count %) 10)))

(s/def ::bas1 (s/and string? #(<= (count %) 40)))

(s/def ::bas2 (s/and string? #(<= (count %) 40)))

(s/def ::baph (s/and string? #(<= (count %) 12)))

(s/def ::countryma (s/and string? #(<= (count %) 2)))

(s/def ::stprma (s/and string? #(<= (count %) 2)))
(s/def ::cityma (s/and string? #(<= (count %) 30)))

(s/def ::zipma (s/and string? #(<= (count %) 10)))

(s/def ::mas1 (s/and string? #(<= (count %) 40)))

(s/def ::mas2 (s/and string? #(<= (count %) 40)))

(s/def ::countryinc (s/and string? #(<= (count %) 3)))

(s/def ::stprinc (s/and string? #(<= (count %) 2)))

(s/def ::ein #(number? (read-string %)))

(s/def ::former (s/and string? #(<= (count %) 150)))

(s/def ::changed (s/and string? #(<= (count %) 8)))

(s/def ::afs (s/and string? #(<= (count %) 5)))

(s/def ::wksi #(number? (read-string %)))

(s/def ::fye (s/and string? #(<= (count %) 4)))

(s/def ::form (s/and string? #(<= (count %) 10)))

;; Could be better specified with a DateTimeParseException
(s/def ::period (s/and string?
                       #(<= (count %) 8)
                       #(try (jtime/local-date "yyyyMMdd" %)
                             (catch Exception e
                                 false))))

(s/def ::fy (s/and string? #(<= (count %) 4)))

(s/def ::fp (s/and string? #(<= (count %) 2)))

(s/def ::filed (s/and string?
                      #(<= (count %) 8)
                      #(try (jtime/local-date "yyyyMMdd" %)
                            (catch Exception e
                              false))))

(s/def ::accepted (s/and string?
                         #(<= (count %) 24)
                         #(try (jtime/local-date-time "yyyy-MM-dd HH:mm:ss.S" %)
                               (catch Exception e
                                 false))))

(s/def ::prevrpt #(number? (read-string %)))

(s/def ::coreg (s/and string? #(<= (count %) 256)))

(s/def ::detail #(number? (read-string %)))

(s/def ::instance (s/and string? #(<= (count %) 32)))

(s/def ::nciks #(number? (read-string %)))

(s/def ::aciks (s/and string? #(<= (count %) 120)))

(s/def ::datatype (s/and string? #(<= (count %) 20)))

(s/def ::iord (s/and string? #(<= (count %) 1)))

(s/def ::crdr (s/and string? #(<= (count %) 1)))

(s/def ::tlabel (s/and string? #(<= (count %) 512)))

(s/def ::doc (s/and string? #(<= (count %) 2048)))

(s/def ::ddate (s/and string?
                      #(<= (count %) 8)
                      #(try (jtime/local-date "yyyyMMdd" %)
                            (catch Exception e
                              false))))

(s/def ::qtrs #(number? (read-string %)))

(s/def ::uom (s/and string? #(<= (count %) 20)))

(s/def ::value (s/and string? #(not (st/blank? %)) #(number? (read-string %))))

(s/def ::footnote string?)

(s/def ::report #(number? (read-string %)))

(s/def ::line #(number? (read-string %)))

(s/def ::stmt (s/and string? #(<= (count %) 2)))

(s/def ::inpth #(number? (read-string %)))

(s/def ::rfile (s/and string? #(<= (count %) 1)))

(s/def ::plabel (s/and string? #(<= (count %) 512)))

(s/def ::custom #(number? (read-string %)))

(s/def ::abstract #(number? (read-string %)))

;; Validations

(s/def :unq/num
  (s/keys :req-un [::adsh ::tag ::version ::ddate ::qtrs ::uom]
          :opt-un [::coreg ::value ::footnote]))

(s/def :unq/pre
  (s/keys :req-un [::adsh ::report ::line ::stmt ::inpth ::rfile ::tag ::version ::plabel]))

(s/def :unq/tag
  (s/keys :req-un [::tag ::version ::custom ::abstract ::iord]
          :opt-un [::datatype ::crdr ::tlabel ::doc]))

(s/def :unq/sub
  (s/keys :req-un [::adsh ::cik ::name ::countryba ::cityba ::countryinc ::wksi ::fye ::form ::period ::fy ::fp ::filed ::accepted ::prevrpt ::detail ::instance ::nciks]
          ::opt-un [::sic ::stprba ::zipba ::bas1 ::bas2 ::baph ::countryma ::stprma ::cityma ::zipma ::mas1 ::mas2 ::stprinc ::ein ::former ::changed ::afs ::aciks]))

;; Constructors
;; TODO: add an intermediate transformation layer between csv data and data passed to
;; constructors

;; TODO: Fix booleans. Failing upon db write.

(defn string-or-nil
  "Returns lambda that returns a string value if present or nil"
  []
  #(if (not (empty? %)) % nil))

(defn number-or-nil
  "Returns lambda that returns a number value from string if present or nil"
  []
  #(if (not (empty? %))
     (try (read-string %)
          (catch Exception e
            (do (println (str "Invalid number: " %))
                nil)))
     nil))

(defn date-or-nil
  "Returns lambda that returns date value from string if present or nil"
  []
  #(if (not (empty? %1)) (jtime/local-date %2 %1) nil))

(defn datetime-or-nil
  "Returns lambda that returns datetime value from string if present or nil"
  []
  #(if (not (empty? %1)) (jtime/local-date-time %2 %1) nil))

(defn boolean-or-nil
  "Returns a lambda that returns a boolean value from a string being either 1 or 0"
  []
  #(if (not (empty? %))
     (if (= (read-string %) 1) true false)
     nil))

(defn create-num
  "Creates a new Num record from a map with all-string values from csv"
  [num]
  {:pre [(s/valid? :unq/num num)]}
  (let [{:keys [adsh
                tag
                version
                coreg
                ddate
                qtrs
                uom
                value
                footnote]} num]
    (->Num ((string-or-nil) adsh)
           ((string-or-nil) tag)
           ((string-or-nil) version)
           ((string-or-nil) coreg)
           ((date-or-nil) ddate "yyyyMMdd")
           ((number-or-nil) qtrs)
           ((string-or-nil) uom)
           ((number-or-nil) value)
           ((string-or-nil) footnote))))

(defn create-pre
  [pre]
  {:pre [(s/valid? :unq/pre pre)]}
  (let [{:keys [adsh
                report
                line
                stmt
                inpth
                rfile
                tag
                version
                plabel]} pre]
    (->Pre ((string-or-nil) adsh)
           ((string-or-nil) report)
           ((number-or-nil) line)
           ((string-or-nil) stmt)
           ((boolean-or-nil) inpth)
           ((string-or-nil) rfile)
           ((string-or-nil) tag)
           ((string-or-nil) version)
           ((string-or-nil) plabel))))

(defn create-tag
  [tag]
  {:pre [(s/valid? :unq/tag tag)]}
  (let [{:keys [tag
                version
                custom
                abstract
                datatype
                iord
                crdr
                tlabel
                doc]} tag]
    (->Tag ((string-or-nil) tag)
           ((string-or-nil) version)
           ((boolean-or-nil) custom)
           ((boolean-or-nil) abstract)
           ((string-or-nil) datatype)
           ((string-or-nil) iord)
           ((string-or-nil) crdr)
           ((string-or-nil) tlabel)
           ((string-or-nil) doc))))

(defn create-sub
  [sub]
  {:pre [(s/valid? :unq/sub sub)]}
  (let [{:keys [adsh
                cik
                name
                sic
                countryba
                stprba
                cityba
                zipba
                bas1
                bas2
                baph
                countryma
                stprma
                cityma
                zipma
                mas1
                mas2
                countryinc
                stprinc
                ein
                former
                changed
                afs
                wksi
                fye
                form
                period
                fy
                fp
                filed
                accepted
                prevrpt
                detail
                instance
                nciks
                aciks]} sub]
    (->Sub ((string-or-nil) adsh)
           ((string-or-nil) cik)
           ((string-or-nil) name)
           ((string-or-nil) sic)
           ((string-or-nil) countryba)
           ((string-or-nil) stprba)
           ((string-or-nil) cityba)
           ((string-or-nil) zipba)
           ((string-or-nil) bas1)
           ((string-or-nil) bas2)
           ((string-or-nil) baph)
           ((string-or-nil) countryma)
           ((string-or-nil) stprma)
           ((string-or-nil) cityma)
           ((string-or-nil) zipma)
           ((string-or-nil) mas1)
           ((string-or-nil) mas2)
           ((string-or-nil) countryinc)
           ((string-or-nil) stprinc)
           ((string-or-nil) ein)
           ((string-or-nil) former)
           ((date-or-nil) changed "yyyyMMdd")
           ((string-or-nil) afs)
           ((boolean-or-nil) wksi)
           ((string-or-nil) fye)
           ((string-or-nil) form)
           ((date-or-nil) period "yyyyMMdd")
           ((string-or-nil) fy)
           ((string-or-nil) fp)
           ((date-or-nil) filed "yyyyMMdd")
           ((datetime-or-nil) accepted "yyyy-MM-dd HH:mm:ss.S")
           ((boolean-or-nil) prevrpt)
           ((boolean-or-nil) detail)
           ((string-or-nil) instance)
           ((string-or-nil) nciks)
           ((string-or-nil) aciks))))

