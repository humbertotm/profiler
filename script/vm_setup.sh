# Early exit on any error with debug mode to see all the commands
set -xe

# Set UTF-8
export LANGUAGE=en_US.UTF-8
export LANG=en_US.UTF-8
export LC_ALL=en_US.UTF-8

# Set encoding to UTF-8
sudo update-locale LANG=en_US.UTF-8

sudo apt update -y
sudo apt-get update -y

sudo apt-get install -y software-properties-common

# Download git
echo "Installing git..."
sudo apt-get install -y git

# -- Install dependencies bash and rlwrap
echo "Installing bash and rlwrap..."
sudo apt-get install -y bash curl rlwrap

# -- Java 11 (AdoptOpenJDK) => recommended for clojure integration
echo "Fetchin GPG key from adoptopenjdk"
wget -qO - https://adoptopenjdk.jfrog.io/adoptopenjdk/api/gpg/key/public | sudo apt-key add -

# Adding adoptopenjdk repository and ensuring it's up to date
sudo add-apt-repository --yes https://adoptopenjdk.jfrog.io/adoptopenjdk/deb/
sudo apt-get update

echo "Installing Java 11..."
sudo apt-get install -y adoptopenjdk-11-hotspot

# -- Install Clojure (from Clojure's official documentation)
curl -O https://download.clojure.org/install/linux-install-1.10.1.483.sh
sudo chmod +x linux-install-1.10.1.483.sh
sudo ./linux-install-1.10.1.483.sh

# -- Installing Leiningen package manager
echo "Installing Leiningen package manager..."

# Download latest stable version of the leiningen installer script
curl https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein > lein

# Moving it to the user programs dir
sudo mv lein /usr/local/bin/lein

# Making it executable
sudo chmod a+x /usr/local/bin/lein

# Python Installation (3.8.5)
# - Python 3.8.5
# - Virtual Env
sudo apt-get install python3 python3-dev python3-venv

# DON'T INSTALL PIP. IT DOES NOT PLAY WELL WITH DEBIAN. INSTALL IT FROM WITHIN YOUR VENV.
# http://thefourtheye.in/2014/12/30/Python-venv-problem-with-ensurepip-in-Ubuntu/
# See https://askubuntu.com/questions/879437/ensurepip-is-disabled-in-debian-ubuntu-for-the-system-python

# Set python version to downloaded version
# This is flaky. Might need to find out the downloaded version first.
echo "alias python='/usr/bin/python3.5'" >> ~/.bashrc

# Install PostgreSQL (9.5)
# TODO: Upgrade to 10 or 11
# Installing newer versions will require a bit more research, ran into some trouble
echo "Install PostgreSQL and set up db..."
sudo apt-get -y install postgresql postgresql-contrib

# Setup database
echo "Setting up db..."
sudo -u postgres createuser screeneruser
echo "ALTER USER screeneruser WITH PASSWORD 'screeneruser';" | sudo -u postgres psql
echo "ALTER USER screeneruser WITH SUPERUSER;" | sudo -u postgres psql

# To better understand, https://www.postgresql.org/docs/9.3/manage-ag-templatedbs.html
echo "update pg_database set datallowconn = TRUE where datname = 'template0';"| sudo -u postgres psql
echo "update pg_database set datistemplate = FALSE where datname = 'template1'; drop database template1;" | sudo -u postgres psql template0
echo "create database template1 with ENCODING = 'UTF-8' LC_CTYPE = 'en_US.utf8' LC_COLLATE = 'en_US.utf8' template = template0;" | sudo -u postgres psql template0
echo "update pg_database set datistemplate = TRUE where datname = 'template1';" | sudo -u postgres psql template0
echo "update pg_database set datallowconn = FALSE where datname = 'template0';" | sudo -u postgres psql template1

# TODO: put db creation and migration tasks in a separate script. 
sudo -u postgres createdb -O screeneruser screener_dev
sudo -u postgres createdb -O screeneruser screener_test

# NEXT:
# To log into screener_dev db: sudo -u screeneruser psql screener_dev
# -- Create db tables based off EDGAR's datasets
# -- Populate db tables from datasets csv

# **********
# To allow project local db connection fix local pg_hba.conf authentication method
# peer => md5
# Restart after making changes to pg_hba.conf
# sudo service postgresql restart

echo "Done! You're good to go!"

