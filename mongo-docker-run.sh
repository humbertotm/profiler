# docker mongo container full init command
# Keeping it here while I experiment
docker run -d -p 27017:27017 \
       -v /tmp/lib/MongoDB:/data/db \
       -v /Users/htellechea/p_projects/screener/docker-mongo-init.js:/docker-entrypoint-initdb.d/init.js \
       --name screener-mongodb \
       -e MONGO_INITDB_ROOT_USERNAME=mongoadmin \
       -e MONGO_INITDB_ROOT_PASSWORD=mongoadmin \
       -e MONGO_INITDB_DATABASE=admin \
       mongo
