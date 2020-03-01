(ns screener.models.validations
  (:require [clojure.spec.alpha :as s]
            [java-time :as jtime]
            [clojure.string :as str]))

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

(s/def ::value (s/and string? #(not (str/blank? %)) #(number? (read-string %))))

(s/def ::footnote string?)

(s/def ::report #(number? (read-string %)))

(s/def ::line #(number? (read-string %)))

(s/def ::stmt (s/and string? #(<= (count %) 2)))

(s/def ::inpth #(number? (read-string %)))

(s/def ::rfile (s/and string? #(<= (count %) 1)))

(s/def ::plabel (s/and string? #(<= (count %) 512)))

(s/def ::custom #(number? (read-string %)))

(s/def ::abstract #(number? (read-string %)))

;; Record validations

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
