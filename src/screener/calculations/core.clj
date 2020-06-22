(ns screener.calculations.core
  (:require [screener.data.tickers :as tickers]
            [screener.data.num :as num]
            [screener.data.sub :as sub]))

(defn ratio
  [divisor dividend]
  (if (or (nil? divisor)
          (or (nil? dividend)
              (zero? dividend)))
    nil
    (with-precision 3 (/ (bigdec divisor) (bigdec dividend)))))

(defn profile-component-keys
  ""
  [year]
  {:current-assets (keyword (str "AssetsCurrent|" year))
   :current-liabilities (keyword (str "LiabilitiesCurrent|" year))
   :accounts-payable (keyword (str "AccountsPayableCurrent|" year))
   :total-liabilities (keyword (str "Liabilities|" year))
   :total-assets (keyword (str "Assets|" year))
   :goodwill (keyword (str "Goodwill|" year))
   :depreciation (keyword (str "DepreciationDepletionAndAmortization|" year))
   :capital-expenditures (keyword (str "CapitalExpenditures|" year))
   :net-income (keyword (str "NetIncomeLoss|" year))
   :total-equity (keyword (str "StockholdersEquity|" year))})

;; ---- PROFILE DESCRIPTOR CALCULATORS ----

;; This calculations assume that the number maps for the submission of interest
;; are already cached. Otherwise, this will blow up.

(defn tangible-assets
  [total-assets goodwill]
  (if (or (nil? total-assets)
          (nil? goodwill))
    nil
    (- (float total-assets) (float goodwill))))

(defn free-cash-flow
  [net-income depreciation-and-amortization capital-expenditures]
  (if (or (nil? net-income)
          (nil? depreciation-and-amortization)
          (nil? capital-expenditures))
    nil
    (+ (- (float net-income) (float depreciation-and-amortization))
       (float capital-expenditures))))

(defn working-capital
  [current-assets current-liabilities]
  (if (or (nil? current-assets)
          (nil? current-liabilities))
    nil
    (- (float current-assets) (float current-liabilities))))

(defn current-assets-to-current-liabilities
  ""
  [adsh year]
  (let [submission-numbers (get-in @num/numbers-cache [(keyword adsh)])
        current-assets-key (:current-assets (profile-component-keys year))
        current-liabilities-key (:current-liabilities (profile-component-keys year))
        current-assets-value (:value (current-assets-key submission-numbers))
        current-liabilities-value (:value (current-liabilities-key submission-numbers))]
    (ratio current-assets-value current-liabilities-value)))

(defn accounts-payable-to-current-assets
  ""
  [adsh year]
  (let [submission-numbers (get-in @num/numbers-cache [(keyword adsh)])
        accounts-payable-key (:accounts-payable (profile-component-keys year))
        current-assets-key (:current-assets (profile-component-keys year))
        current-assets-value (:value (current-assets-key submission-numbers))
        accounts-payable-value (:value (accounts-payable-key submission-numbers))]
    (ratio accounts-payable-value current-assets-value)))

(defn current-assets-to-total-liabilities
  ""
  [adsh year]
  (let [submission-numbers (get-in @num/numbers-cache [(keyword adsh)])
        current-assets-key (:current-assets (profile-component-keys year))
        total-liabilities-key (:total-liabilities (profile-component-keys year))
        current-assets-value (:value (current-assets-key submission-numbers))
        total-liabilities-value (:value (total-liabilities-key submission-numbers))]
    (ratio current-assets-value total-liabilities-value)))

(defn total-tangible-assets-to-total-liabilities
  ""
  [adsh year]
  (let [submission-numbers (get-in @num/numbers-cache [(keyword adsh)])
        total-assets-key (:total-assets (profile-component-keys year))
        goodwill-key (:goodwill (profile-component-keys year))
        total-liabilities-key (:total-liabilities (profile-component-keys year))
        total-assets-value (:value (total-assets-key submission-numbers))
        goodwill-value (:value (goodwill-key submission-numbers))
        total-liabilities-value (:value (total-liabilities-key submission-numbers))
        tangible-assets-value (tangible-assets total-assets-value goodwill-value)]
    (ratio tangible-assets-value total-liabilities-value)))

(defn goodwill-to-total-assets
  ""
  [adsh year]
  (let [submission-numbers (get-in @num/numbers-cache [(keyword adsh)])
        goodwill-key (:goodwill (profile-component-keys year))
        total-assets-key (:total-assets (profile-component-keys year))
        total-assets-value (:value (total-assets-key submission-numbers))
        goodwill-value (:value (goodwill-key submission-numbers))]
    (ratio goodwill-value total-assets-value)))

(defn net-income
  ""
  [adsh year]
  (let [submission-numbers (get-in @num/numbers-cache [(keyword adsh)])
        net-income-key (:net-income (profile-component-keys year))]
    (:value (net-income-key submission-numbers))))

(defn return-on-equity
  ""
  [adsh year]
  (let [submission-numbers (get-in @num/numbers-cache [(keyword adsh)])
        net-income-key (:net-income (profile-component-keys year))
        total-equity-key (:total-equity (profile-component-keys year))
        net-income-value (:value (net-income-key submission-numbers))
        total-equity-value (:value (total-equity-key submission-numbers))]
    (ratio net-income-value total-equity-value)))

(defn return-on-working-capital
  ""
  [adsh year]
  (let [submission-numbers (get-in @num/numbers-cache [(keyword adsh)])
        net-income-key (:net-income (profile-component-keys year))
        current-assets-key (:current-assets (profile-component-keys year))
        current-liabilities-key (:current-liabilities (profile-component-keys year))
        net-income-value (:value (net-income-key submission-numbers))
        current-assets-value (:value (current-assets-key submission-numbers))
        current-liabilities-value (:value (current-liabilities-key submission-numbers))
        working-capital-value (working-capital current-assets-value current-liabilities-value)]
    (ratio net-income-value working-capital-value)))

;; ---- PROFILE DESCRIPTOR CALCULATORS ----



(defn build-profile
  "Builds a simplistic financial profile based on a provided list of Numbers and year.
   Caveat: expects Numbers hash maps as inputs."
  [sub-numbers year]
  (let [profile-keys (profile-component-keys year)
        current-assets (:value ((:current-assets profile-keys) sub-numbers))
        current-liabilities (:value ((:current-liabilities profile-keys) sub-numbers))
        accounts-payable (:value ((:accounts-payable profile-keys) sub-numbers))
        total-liabilities (:value ((:total-liabilities profile-keys) sub-numbers))
        total-assets (:value ((:total-assets profile-keys) sub-numbers))
        goodwill (:value ((:goodwill profile-keys) sub-numbers))
        depreciation-and-amortization (:value ((:depreciation profile-keys) sub-numbers))
        capital-expenditures (:value ((:capital-expenditures profile-keys) sub-numbers))
        net-income (:value ((:net-income profile-keys) sub-numbers))
        total-equity (:value ((:total-equity profile-keys) sub-numbers))
        working-capital (working-capital current-assets current-liabilities)
        total-tangible-assets (tangible-assets total-assets goodwill)]
    {:CurrentAssetsToCurrentLiabilities (ratio current-assets current-liabilities)
     :AccountsPayableToCurrentAssets (ratio accounts-payable current-assets)
     :CurrentAssetsToTotalLiabilities (ratio current-assets total-liabilities)
     :TotalTangibleAssetsToTotalLiabilities (ratio total-tangible-assets total-liabilities)
     :GoodwillToTotalAssets (ratio goodwill total-assets)
     :NetIncome net-income
     :ReturnOnEquity (ratio net-income total-equity)
     :ReturnOnWorkingCapital (ratio net-income working-capital)
     :FreeCashFlow (free-cash-flow
                    net-income
                    depreciation-and-amortization
                    capital-expenditures)}))

(defn profile-company
  "Returns a financial profile for specified company in specified year"
  [ticker year]
  (-> (tickers/get-ticker-cik-mapping ticker)
      (:cik)
      (sub/fetch-form-adsh-for-cik-year "10-K" year)
      (num/fetch-numbers-for-submission)
      (build-profile year)))

