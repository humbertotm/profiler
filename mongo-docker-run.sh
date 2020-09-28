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

# Start screener-clj container linked to mongodb container
docker run -d -p 36096:36096 \
       --link screener-mongodb:screenermdb \
       --name screener-clj \
       screener-clj:latest

# Start postgresql instance
docker run -d -p 5432:5432 \
       -v /Users/htellechea/p_projects/screener/db/create_tables.sql:/usr/local/etc/create_tables.sql \
       -v /Users/htellechea/p_projects/screener/docker_init/postgres:/docker-entrypoint-initdb.d \
       --name screener-postgres \
       -e POSTGRES_USER=postgres \
       -e POSTGRES_PASSWORD=postgres \
       postgres:latest
       
