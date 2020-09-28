#!/bin/bash

set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    UPDATE pg_database SET datistemplate = FALSE WHERE datname = 'template0';
    UPDATE pg_database SET datistemplate = FALSE WHERE datname = 'template1';
    DROP DATABASE template1;
    CREATE DATABASE template1 WITH ENCODING = 'UTF-8' LC_CTYPE = 'en_US.UTF8' LC_COLLATE = 'en_US.UTF8' TEMPLATE = template0;
    UPDATE pg_database SET datistemplate = TRUE WHERE datname = 'template1';
    UPDATE pg_database SET datistemplate = FALSE WHERE datname = 'template0';

    CREATE DATABASE screener_dev;
    
    CREATE USER screeneruser;
    ALTER USER screeneruser WITH PASSWORD 'screeneruser';
    GRANT ALL PRIVILEGES ON DATABASE screener_dev TO screeneruser;
EOSQL

