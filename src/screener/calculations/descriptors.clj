(ns screener.calculations.descriptors
  (:require [screener.calculations.operations :refer :all]))

(def data-tags
  "Returns a mapping of financial descriptor to tag employed to identify them (as per SEC
   datasets spec) in cache."
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

(def args-spec
  "Defines a mapping of descriptor to list of arguments spec required to compute them. Args
   :name should match a key in profile-descriptor-tags map if :type is :plain-number. If
   :type is :computed, :plain-number arguments can be reached recursively."
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
;; TODO: this calculations can be incorporated in the map above as an additional key
;; specifying how the descriptor is to be computed off some basic operation.

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

(defn current-assets-to-current-liabilities
  "Current assets / current liabilities"
  [{:keys [current-assets current-liabilities]}]
  (ratio current-assets current-liabilities))

(defn accounts-payable-to-current-assets
  "Accounts payable / current assets"
  [{:keys [accounts-payable current-assets]}]
  (ratio accounts-payable current-assets))

(defn current-assets-to-total-liabilities
  "Current assets / total liabilities"
  [{:keys [current-assets total-liabilities]}]
  (ratio current-assets total-liabilities))

(defn total-tangible-assets-to-total-liabilities
  "tangible assets / total liabilities"
  [{:keys [tangible-assets total-liabilities]}]
  (ratio tangible-assets total-liabilities))

(defn goodwill-to-total-assets
  "goodwill / total assets"
  [{:keys [goodwill total-assets]}]
  (ratio goodwill total-assets))

(defn net-income
  "Net income"
  [{:keys [net-income]}]
  net-income)

(defn return-on-equity
  "Net income / total equity"
  [{:keys [net-income total-equity]}]
  (ratio net-income total-equity))

(defn return-on-working-capital
  "Net income / working capital"
  [{:keys [net-income working-capital]}]
  (ratio net-income working-capital))

