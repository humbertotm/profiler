(ns screener.calculations.core
  (:require [screener.models.tickers :as tickers]
            [screener.models.num :as num]
            [screener.models.sub :as sub]))

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

(defn current-assets-to-current-liabilities
  [current-assets current-liabilities]
  (if (or (nil? current-assets)
          (nil? current-liabilities))
    nil
    (/ (float current-assets) (float current-liabilities))))

(defn accounts-payable-to-current-assets
  [accounts-payable current-assets]
  (if (or (nil? accounts-payable)
          (nil? current-assets))
    nil
    (/ (float accounts-payable) (float current-assets))))

(defn current-assets-to-total-liabilities
  [current-assets total-liabilities]
  (if (or (nil? current-assets)
          (nil? total-liabilities))
    nil
    (/ (float current-assets) (float total-liabilities))))

(defn tangible-assets
  [total-assets goodwill]
  (if (or (nil? total-assets)
          (nil? goodwill))
    nil
    (- (float total-assets) (float goodwill))))

(defn total-tangible-assets-to-total-liabilities
  [tangible-assets total-liabilities]
  (if (or (nil? tangible-assets)
          (nil? total-liabilities))
    nil
    (/ (float tangible-assets) (float total-liabilities))))

(defn goodwill-to-total-assets
  [goodwill total-assets]
  (if (or (nil? goodwill)
          (nil? total-assets))
    nil
    (/ (float goodwill) (float total-assets))))

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

(defn return-on-equity
  [net-income total-equity]
  (if (or (nil? net-income)
          (nil? total-equity))
    nil
    (/ (float net-income) (float total-equity))))

(defn return-on-working-capital
  [net-income working-capital]
  (if (or (nil? net-income)
          (nil? working-capital))
    nil
    (/ (float net-income) (float working-capital))))

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

    {:CurrentAssetsToCurrentLiabilities (current-assets-to-current-liabilities
                                         current-assets
                                         current-liabilities)
     :AccountsPayableToCurrentAssets (accounts-payable-to-current-assets
                                      accounts-payable
                                      current-assets)
     :CurrentAssetsToTotalLiabilities (current-assets-to-total-liabilities
                                       current-assets
                                       total-liabilities)
     :TotalTangibleAssetsToTotalLiabilities (total-tangible-assets-to-total-liabilities
                                             total-tangible-assets
                                             total-liabilities)
     :GoodwillToTotalAssets (goodwill-to-total-assets
                             goodwill
                             total-assets)
     :FreeCashFlow (free-cash-flow
                    net-income
                    depreciation-and-amortization
                    capital-expenditures)
     :NetIncome net-income
     :ReturnOnEquity (return-on-equity
                      net-income
                      total-equity)
     :ReturnOnWorkingCapital (return-on-working-capital
                              net-income
                              working-capital)}))

(defn profile-company
  "Returns a financial profile for specified company in specified year"
  [ticker year]
  (-> (tickers/get-ticker-cik-mapping ticker)
      (:cik)
      (sub/fetch-form-adsh-for-cik-year "10-K" year)
      (num/fetch-numbers-for-submission)
      (build-profile year)))

