(ns screener.calculations.core
  (:require [screener.data.tickers :as tickers]
            [screener.data.num :as num]
            [screener.data.sub :as sub]))

(defn ratio
  "A simple ratio calculation that does some minimal validation on inputs. Returns a nil
   value if ratio cannot be calculated.
   Formats return value to display two decimal places."
  [divisor dividend]
  (if (or (nil? divisor)
          (or (nil? dividend)
              (zero? dividend)))
    nil
    (with-precision 3 (/ (bigdec divisor) (bigdec dividend)))))

(def profile-descriptor-tags
  "Returns a mapping of financial descriptor to Tag|year key employed to look up for cached
   numbers."
  {:current-assets "AssetsCurrent",
   :current-liabilities "LiabilitiesCurrent",
   :accounts-payable "AccountsPayableCurrent",
   :total-liabilities "Liabilities",
   :total-assets "Assets",
   :goodwill "Goodwill",
   :depreciation "DepreciationDepletionAndAmortization",
   :capital-expenditures "CapitalExpenditures",
   :net-income "NetIncomeLoss",
   :total-equity "StockholdersEquity"})

(def descriptor-args-spec
  {:tangible-assets '({:name :total-assets, :type :plain-number},
                      {:name :goodwill, :type :plain-number}),
   :free-cash-flow '({:name :net-income, :type :plain-number},
                     {:name :depreciation, :type :plain-number},
                     {:name :capital-expenditures, :type :plain-number}),
   :working-capital '({:name :current-assets, :type :plain-number},
                      {:name :current-liabilities, :type :plain-number}),
   :current-assets-to-current-liabilities '({:name :current-assets, :type :plain-number},
                                            {:name :current-liabilities, :type :plain-number}),
   :accounts-payable-to-current-assets '({:name :accounts-payable, :type :plain-number},
                                         {:name :current-assets, :type :plain-number}),
   :current-assets-to-total-liabilities '({:name :current-assets, :type :plain-number},
                                          {:name :total-liabilities, :type :plain-number}),
   :total-tangible-assets-to-total-liabilities '({:name :tangible-assets, :type :computed},
                                                 {:name :total-liabilities, :type :plain-number}),
   :goodwill-to-total-assets '({:name :goodwill, :type :plain-number},
                               {:name :total-assets, :type :plain-number}),
   :net-income '({:name :net-income, :type :plain-number}),
   :return-on-equity '({:name :net-income, :type :plain-number},
                       {:name :total-equity, :type :plain-number}),
   :return-on-working-capital '({:name :net-income, :type :plain-number},
                                {:name :working-capital, :type :computed})
   })

;; ---- PROFILE DESCRIPTOR CALCULATORS ----

;; This calculations assume that the number maps for the submission of interest
;; are already cached. Otherwise, this will blow up.

(defn tangible-assets
  "Total assets - goodwill"
  [{:keys [total-assets goodwill]}]
  (if (or (nil? total-assets)
          (nil? goodwill))
    nil
    (- (float total-assets) (float goodwill))))

(defn free-cash-flow
  "Net income - depreciation + capital expenditures"
  [{:keys [net-income depreciation-and-amortization capital-expenditures]}]
  (if (or (nil? net-income)
          (nil? depreciation-and-amortization)
          (nil? capital-expenditures))
    nil
    (+ (- (float net-income) (float depreciation-and-amortization))
       (float capital-expenditures))))

(defn working-capital
  "Current assets - current liabilities"
  [{:keys [current-assets current-liabilities]}]
  (if (or (nil? current-assets)
          (nil? current-liabilities))
    nil
    (- (float current-assets) (float current-liabilities))))

;; (defn current-assets-to-current-liabilities
;;   "Current assets / current liabilities"
;;   [adsh year]
;;   (let [submission-numbers (get-in @num/numbers-cache [(keyword adsh)])
;;         current-assets-key (:current-assets (profile-descriptor-keys year))
;;         current-liabilities-key (:current-liabilities (profile-descriptor-keys year))
;;         current-assets-value (:value (current-assets-key submission-numbers))
;;         current-liabilities-value (:value (current-liabilities-key submission-numbers))]
;;     (ratio current-assets-value current-liabilities-value)))

(defn current-assets-to-current-liabilities
  "Current assets / current liabilities"
  [{:keys [current-assets current-liabilities]}]
  (ratio current-assets current-liabilities))

(defn accounts-payable-to-current-assets
  "Accounts payable / current assets"
  [{:keys [accounts-payable current-assets]}]
  (ratio accounts-payable current-assets))

;; (defn accounts-payable-to-current-assets
;;   "Accounts payable / current assets"
;;   [adsh year]
;;   (let [submission-numbers (get-in @num/numbers-cache [(keyword adsh)])
;;         accounts-payable-key (:accounts-payable (profile-descriptor-keys year))
;;         current-assets-key (:current-assets (profile-descriptor-keys year))
;;         current-assets-value (:value (current-assets-key submission-numbers))
;;         accounts-payable-value (:value (accounts-payable-key submission-numbers))]
;;     (ratio accounts-payable-value current-assets-value)))

;; (defn current-assets-to-total-liabilities
;;   "Current assets / total liabilities"
;;   [adsh year]
;;   (let [submission-numbers (get-in @num/numbers-cache [(keyword adsh)])
;;         current-assets-key (:current-assets (profile-descriptor-keys year))
;;         total-liabilities-key (:total-liabilities (profile-descriptor-keys year))
;;         current-assets-value (:value (current-assets-key submission-numbers))
;;         total-liabilities-value (:value (total-liabilities-key submission-numbers))]
;;     (ratio current-assets-value total-liabilities-value)))

(defn current-assets-to-total-liabilities
  "Current assets / total liabilities"
  [{:keys [current-assets total-liabilities]}]
  (ratio current-assets total-liabilities))

;; (defn total-tangible-assets-to-total-liabilities
;;   "tangible assets / total liabilities"
;;   [adsh year]
;;   (let [submission-numbers (get-in @num/numbers-cache [(keyword adsh)])
;;         total-assets-key (:total-assets (profile-descriptor-keys year))
;;         goodwill-key (:goodwill (profile-descriptor-keys year))
;;         total-liabilities-key (:total-liabilities (profile-descriptor-keys year))
;;         total-assets-value (:value (total-assets-key submission-numbers))
;;         goodwill-value (:value (goodwill-key submission-numbers))
;;         total-liabilities-value (:value (total-liabilities-key submission-numbers))
;;         tangible-assets-value (tangible-assets total-assets-value goodwill-value)]
;;     (ratio tangible-assets-value total-liabilities-value)))

(defn total-tangible-assets-to-total-liabilities
  "tangible assets / total liabilities"
  [{:keys [tangible-assets total-liabilities]}]
  (ratio tangible-assets total-liabilities))

;; (defn goodwill-to-total-assets
;;   "goodwill / total assets"
;;   [adsh year]
;;   (let [submission-numbers (get-in @num/numbers-cache [(keyword adsh)])
;;         goodwill-key (:goodwill (profile-descriptor-keys year))
;;         total-assets-key (:total-assets (profile-descriptor-keys year))
;;         total-assets-value (:value (total-assets-key submission-numbers))
;;         goodwill-value (:value (goodwill-key submission-numbers))]
;;     (ratio goodwill-value total-assets-value)))

(defn goodwill-to-total-assets
  "goodwill / total assets"
  [{:keys [goodwill total-assets]}]
  (ratio goodwill total-assets))

;; (defn net-income
;;   "Net income"
;;   [adsh year]
;;   (let [submission-numbers (get-in @num/numbers-cache [(keyword adsh)])
;;         net-income-key (:net-income (profile-descriptor-keys year))]
;;     (:value (net-income-key submission-numbers))))

(defn net-income
  "Net income"
  [{:keys [net-income]}]
  net-income)

;; (defn return-on-equity
;;   "Net income / total equity"
;;   [adsh year]
;;   (let [submission-numbers (get-in @num/numbers-cache [(keyword adsh)])
;;         net-income-key (:net-income (profile-descriptor-keys year))
;;         total-equity-key (:total-equity (profile-descriptor-keys year))
;;         net-income-value (:value (net-income-key submission-numbers))
;;         total-equity-value (:value (total-equity-key submission-numbers))]
;;     (ratio net-income-value total-equity-value)))

(defn return-on-equity
  "Net income / total equity"
  [{:keys [net-income total-equity]}]
  (ratio net-income total-equity))


;; (defn return-on-working-capital
;;   "Net income / working capital"
;;   [adsh year]
;;   (let [submission-numbers (get-in @num/numbers-cache [(keyword adsh)])
;;         net-income-key (:net-income (profile-descriptor-keys year))
;;         current-assets-key (:current-assets (profile-descriptor-keys year))
;;         current-liabilities-key (:current-liabilities (profile-descriptor-keys year))
;;         net-income-value (:value (net-income-key submission-numbers))
;;         current-assets-value (:value (current-assets-key submission-numbers))
;;         current-liabilities-value (:value (current-liabilities-key submission-numbers))
;;         working-capital-value (working-capital current-assets-value current-liabilities-value)]
;;     (ratio net-income-value working-capital-value)))

(defn return-on-working-capital
  "Net income / working capital"
  [{:keys [net-income working-capital]}]
  (ratio net-income working-capital))

