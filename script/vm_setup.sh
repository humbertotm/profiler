# Generate locale in case it's missing
sudo locale-gen en_US.UTF-8

sudo apt update -y
sudo apt-get update -y 		# -y for silent update


sudo apt-get install -y software-properties-common

# Download git
echo "Installing git..."
sudo apt-get install -y git	# -y for silent installation



# -- Install dependencies bash and rlwrap
echo "Installing bash and rlwrap..."
sudo apt-get install -y bash curl rlwrap

# -- Java 11 (AdoptOpenJDK)
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

echo "Done! You're good to go!"

# TODO: what db should I install? Postgres?

# After setup steps

# Setting git global configs
# git config --global user.name "yourusername"
# git config --global user.email "youremail"

