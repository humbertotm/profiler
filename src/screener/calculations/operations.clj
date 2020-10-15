(ns screener.calculations.operations
  (:require [screener.calculations.descriptors :as descriptors]))

;; Declaring these two beforehand as there's a mutually recursive relation between them
(declare build-descriptor-args)
(declare calculate)

(defn get-descriptor-computation-fn
  "Determines the appropriate symbol for a descriptor function from a descriptor string.
   eg. 'Net Income' => #screener.calculations.core/net-income."
  [descriptor-kw]
  (let [computation-fn (:computation-fn (descriptor-kw descriptors/descriptor-spec))]
    (if (not (nil? computation-fn))
      (resolve (symbol (str
                        "screener.calculations.operations/"
                        (name computation-fn))))
      (resolve (symbol "screener.calculations.operations/simple-number")))))

(defmacro calculate
  ""
  [descriptor-kw adsh year numbers]
  `((~get-descriptor-computation-fn ~descriptor-kw)
    (build-descriptor-args ~descriptor-kw ~adsh ~year ~numbers)))

(defn build-descriptor-args
  ""
  [descriptor-kw adsh year numbers]
  (let [descriptor-spec (descriptor-kw descriptors/descriptor-spec)
        computation-fn (:computation-fn descriptor-spec)
        fn-args (:args descriptor-spec)]
    (if (not (nil? computation-fn))
      ((resolve (symbol
                 (str "screener.calculations.operations/build-"
                      (name computation-fn)
                      "-args")))
       {:args-spec fn-args, :adsh adsh, :year year, :numbers numbers})
      (let [tag (:tag (descriptor-kw descriptors/src-number-data-tags))]
        (:value ((keyword (str tag "|" year)) numbers))))))


;; BASIC CALCULATIONS EMPLOYED IN PROFILE DESCRIPTORS
;; AND ITS CORRESPONDING ARGUMENT BUILDING FUNCTION

(defn ratio
  "A simple ratio calculation that does some minimal validation on inputs. Returns a nil
   value if ratio cannot be calculated.
   Formats return value to display two decimal places."
  [{:keys [antecedent consequent]}]
  (if (or (nil? antecedent)
          (or (nil? consequent)
              (zero? consequent)))
    nil
    (/ (double antecedent) (double consequent))))

(defn build-ratio-args
  ""
  [{:keys [args-spec adsh year numbers]}]
  {:antecedent (calculate
                (:name (:antecedent args-spec))
                adsh
                year
                numbers),
   :consequent (calculate
                (:name (:consequent args-spec))
                adsh
                year
                numbers)})

(defn addition
  ""
  [summands-list]
  (if (some nil? summands-list)
    nil
    (reduce (fn
              [accum next]
              (+ accum next))
            0
            summands-list)))

(defn build-addition-args
  ""
  [{:keys [args-spec adsh year numbers]}]
  (reduce (fn
            [accum next]
            (let [sign (:sign next)
                  val (calculate (:name next) adsh year numbers)]
              (cond
                (nil? val) nil
                (= :positive sign) (cons val accum)
                :else (cons (* -1 val) accum))))
          '()
          args-spec))

(defn simple-number
  ""
  [val]
  (if (nil? val)
    nil
    (double val)))

(defn build-simple-number-args
  ""
  [{:keys [args-spec adsh year numbers]}]
  (:value ((keyword
            (str
             (:tag args-spec)
             "|"
             year))
           numbers)))

;; (defn recursion-safe-computation?
;;   "Checks that a fallback function is safe to compute by checking that all required args
;;   for computation are present in submission numbers.
;;   Assumes that fallback function arguments are simple numbers. Should this change, this
;;   failsafe mechanism will have to be adjusted."
;;   [fallback-fn-kw adsh year]
;;   (let [fallback-args (fallback-fn-kw descriptors/args-spec)
;;         sub-numbers (num/fetch-numbers-for-submission adsh)
;;         args-key-list (reduce (fn [accum next]
;;                                 (conj accum
;;                                       (keyword (str
;;                                                 (:tag ((:name next)
;;                                                        descriptors/src-number-data-tags))
;;                                                 "|"
;;                                                 year))))
;;                               '()
;;                               fallback-args)]
;;     (reduce (fn [accum next]
;;               (and accum
;;                    (contains? sub-numbers next)))
;;             true
;;             args-key-list)))

