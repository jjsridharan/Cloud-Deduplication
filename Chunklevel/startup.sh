sudo apt-get update
sudo apt-get install build-essential libssl-dev
sudo apt-get install curl
curl -sL https://raw.githubusercontent.com/creationix/nvm/v0.33.8/install.sh -o install_nvm.sh
sh install_nvm.sh
source ~/.profile
nvm install 9.7.1
npm link express
npm link multer
npm link body-parser
npm link child_process
npm link cookie-parse
npm link rimraf

