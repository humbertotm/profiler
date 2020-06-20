(ns screener.calculations.core
  (:require [screener.data.tickers :as tickers]
            [screener.data.num :as num]
            [screener.data.sub :as sub]))

;; TODO: format output numbers to 2 decimal places
;; Write tests

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

(defn ratio
  [divisor dividend]
  (if (or (nil? divisor)
          (or (nil? dividend)
              (zero? dividend)))
    nil
    (with-precision 3 (/ (bigdec divisor) (bigdec dividend)))))

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

