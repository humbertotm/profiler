;; This is where a production profile with sensitive data would be placed to avoid commiting
;; it to source control.

{:profiles/dev {:env {:db-subname "//localhost:5432/screener_dev"
                      :db-user "screeneruser"
                      :db-password "screeneruser"}}
 :profiles/test {:env {:db-subname "//localhost:5432/screener_test"
                       :db-user "screeneruser"
                       :db-password "screeneruser"}}}
