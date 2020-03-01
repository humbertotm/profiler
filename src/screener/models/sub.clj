(ns screener.models.sub
  (:require [clojure.spec.alpha :as s]
            [screener.models.value-setters :refer :all]
            [screener.models.validations :refer :all]))

(defrecord Sub
    [adsh
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
     aciks])

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

