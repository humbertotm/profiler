(ns screener.models.core
  (:require [clojure.spec.alpha :as s]
            [screener.models.num :refer :all]
            [screener.models.tag :refer :all]
            [screener.models.pre :refer :all]
            [screener.models.sub :refer :all])
  (:import [screener.models.num Num]
           [screener.models.tag Tag]
           [screener.models.pre Pre]
           [screener.models.sub Sub]))

(def records-map {:sub "Sub"
                  :tag "Tag"
                  :num "Num"
                  :pre "Pre"})

(def tables {:sub :submissions
             :tag :tags
             :num :numbers
             :pre :presentations})

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

;; [wololo] A regex for yymmdd date format is necessary for this validation
(s/def ::period (s/and string? #(<= (count %) 8)))

(s/def ::fy (s/and string? #(<= (count %) 4)))

(s/def ::fp (s/and string? #(<= (count %) 2)))

;; [wololo] same regex as for ::period
(s/def ::filed (s/and string? #(<= (count %) 8)))

;; [wololo] regex for yyyy-mm-dd hh::mm::ss
(s/def ::accepted (s/and string? #(<= (count %) 19)))

(s/def ::prevrpt #(number? (read-string %)))

(s/def ::detail #(number? (read-string %)))

(s/def ::instance (s/and string? #(<= (count %) 32)))

(s/def ::nciks #(number? (read-string %)))

(s/def ::aciks (s/and string? #(<= (count %) 120)))

(s/def ::datatype (s/and string? #(<= (count %) 20)))

(s/def ::iord (s/and string? #(<= (count %) 1)))

(s/def ::crdr (s/and string? #(<= (count %) 1)))

(s/def ::tlabel (s/and string? #(<= (count %) 512)))

(s/def ::doc (s/and string? #(<= (count %) 2048)))

(s/def ::coreg #(number? (read-string %)))

;; [wololo] regex yyyymmdd
(s/def ::ddate (s/and string? #(<= (count %) 8)))

(s/def ::qtrs #(number? (read-string %)))

(s/def ::uom (s/and string? #(<= (count %) 20)))

(s/def ::value #(number? (read-string %)))

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
    (->Num adsh
           tag
           version
           (read-string coreg)
           ddate
           (read-string qtrs)
           uom
           (read-string value)
           footnote)))

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
    (->Pre adsh
           (read-string report)
           (read-string line)
           stmt
           (read-string inpth)
           rfile
           tag
           version
           plabel)))

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
    (->Tag tag
           version
           (read-string custom)
           (read-string abstract)
           datatype
           iord
           crdr
           tlabel
           doc)))

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
    (->Sub adsh
           (read-string cik)
           name
           (read-string sic)
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
           (read-string ein)
           former
           changed
           afs
           (read-string wksi)
           fye
           form
           period
           fy
           fp
           filed
           accepted
           (read-string prevrpt)
           (read-string detail)
           instance
           (read-string nciks)
           aciks)))

