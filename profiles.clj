{:dev {:env {:db-subname "//localhost:5432/screener_dev"
             :db-user "screeneruser"
             :db-password "screeneruser"
             :mongo-host-ipaddr "screenermdb" ; This matches the network alias for mongodb container
             :mongo-host-port 27017
             :mongo-auth-db "admin"
             :mongo-username "mongoadmin"
             :mongo-password "mongoadmin"}}}
