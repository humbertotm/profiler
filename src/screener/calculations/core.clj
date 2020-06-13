(ns screener.calculations.core
  (:require [screener.models.tickers :as tickers]
            [screener.models.num :as num]
            [screener.models.sub :as sub]))

;; TODO: format output numbers to 2 decimal places
;; Write tests
;; Refactor

(defn current-assets-to-current-liabilities
  ""
  [current-assets current-liabilities]
  (if (or (nil? current-assets)
          (nil? current-liabilities))
    nil
    (/ (float current-assets) (float current-liabilities))))

(defn accounts-payable-to-current-assets
  ""
  [accounts-payable current-assets]
  (if (or (nil? accounts-payable)
          (nil? current-assets))
    nil
    (/ (float accounts-payable) (float current-assets))))

(defn current-assets-to-total-liabilities
  ""
  [current-assets total-liabilities]
  (if (or (nil? current-assets)
          (nil? total-liabilities))
    nil
    (/ (float current-assets) (float total-liabilities))))

(defn tangible-assets
  ""
  [total-assets goodwill]
  (if (or (nil? total-assets)
          (nil? goodwill))
    nil
    (- (float total-assets) (float goodwill))))

(defn total-tangible-assets-to-total-liabilities
  ""
  [tangible-assets total-liabilities]
  (if (or (nil? tangible-assets)
          (nil? total-liabilities))
    nil
    (/ (float tangible-assets) (float total-liabilities))))

(defn goodwill-to-total-assets
  ""
  [goodwill total-assets]
  (if (or (nil? goodwill)
          (nil? total-assets))
    nil
    (/ (float goodwill) (float total-assets))))

(defn free-cash-flow
  ""
  [net-income depreciation-and-amortization capital-expenditures]
  (if (or (nil? net-income)
          (nil? depreciation-and-amortization)
          (nil? capital-expenditures))
    nil
    (+ (- (float net-income) (float depreciation-and-amortization))
       (float capital-expenditures))))

(defn working-capital
  ""
  [current-assets current-liabilities]
  (if (or (nil? current-assets)
          (nil? current-liabilities))
    nil
    (- (float current-assets) (float current-liabilities))))

(defn return-on-equity
  ""
  [net-income total-equity]
  (if (or (nil? net-income)
          (nil? total-equity))
    nil
    (/ (float net-income) (float total-equity))))

(defn return-on-working-capital
  ""
  [net-income working-capital]
  (if (or (nil? net-income)
          (nil? working-capital))
    nil
    (/ (float net-income) (float working-capital))))

(defn build-profile
  ""
  [sub-numbers year]
  (let [current-assets-tag (keyword (str "AssetsCurrent|" year))
        current-liabilities-tag (keyword (str "LiabilitiesCurrent|" year))
        accounts-payable-tag (keyword (str "AccountsPayableCurrent|" year))
        total-liabilities-tag (keyword (str "Liabilities|" year))
        total-assets-tag (keyword (str "Assets|" year))
        goodwill-tag (keyword (str "Goodwill|" year))
        depreciation-and-amortization-tag (keyword (str "DepreciationDepletionAndAmortization|" year))
        capital-expenditures-tag (keyword (str "CapitalExpenditures|" year))
        net-income-tag (keyword (str "NetIncomeLoss|" year))
        total-equity-tag (keyword (str "StockholdersEquity|" year))
        current-assets (sub-numbers current-assets-tag)
        current-liabilities (sub-numbers current-liabilities-tag)
        accounts-payable (sub-numbers accounts-payable-tag)
        total-liabilities (sub-numbers total-liabilities-tag)
        total-assets (sub-numbers total-assets-tag)
        goodwill (sub-numbers goodwill-tag)
        depreciation-and-amortization (sub-numbers depreciation-and-amortization-tag)
        capital-expenditures (sub-numbers capital-expenditures-tag)
        net-income (sub-numbers net-income-tag)
        total-equity (sub-numbers total-equity-tag)
        working-capital (working-capital (:value current-assets) (:value current-liabilities))
        total-tangible-assets (tangible-assets (:value total-assets) (:value goodwill))]
    {:CurrentAssetsToCurrentLiabilities (current-assets-to-current-liabilities
                                         (:value current-assets)
                                         (:value current-liabilities))
     :AccountsPayableToCurrentAssets (accounts-payable-to-current-assets
                                      (:value accounts-payable)
                                      (:value current-assets))
     :CurrentAssetsToTotalLiabilities (current-assets-to-total-liabilities
                                       (:value current-assets)
                                       (:value total-liabilities))
     :TotalTangibleAssetsToTotalLiabilities (total-tangible-assets-to-total-liabilities
                                             total-tangible-assets
                                             (:value total-liabilities))
     :GoodwillToTotalAssets (goodwill-to-total-assets
                             (:value goodwill)
                             (:value total-assets))
     :FreeCashFlow (free-cash-flow
                    (:value net-income)
                    (:value depreciation-and-amortization)
                    (:value capital-expenditures))
     :NetIncome (:value net-income)
     :ReturnOnEquity (return-on-equity
                      (:value net-income)
                      (:value total-equity))
     :ReturnOnWorkingCapital (return-on-working-capital
                              (:value net-income)
                              working-capital)}))

(defn profile-company
  "Returns a financial profile for specified company in specified year"
  [ticker year]
  (-> (tickers/get-ticker-cik-mapping ticker)
      (:cik)
      (sub/fetch-form-adsh-for-cik-year "10-K" year)
      (num/fetch-numbers-for-submission)
      (build-profile year)))

