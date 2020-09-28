#!/bin/bash

set -e

# This assumes the sql script has already been placed in the expected path through
# a shared volume
psql -v ON_ERROR_STOP=1 \
     --username "$POSTGRES_USER" \
     --dbname "screener_dev" \
     --echo-all \
     --file "/usr/local/etc/create_tables.sql"

