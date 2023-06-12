## Configuration

--------------------------------------------------------------------------------
### Install openjdk-17-jdk: `sudo apt-get install openjdk-17-jdk`

--------------------------------------------------------------------------------
### Create directory: `mkdir /srv/humanode/`
### Go to directory: `cd /srv/humanode/`
### Clone jar from git: `git clone --branch build https://github.com/gicu-adasanu/humanode-alert.git`
### Edit application.properties and put your Telegram bot token: `bot.token=your_bot_token`
### For the first time the chat id will be 0. After you start the application you will send a message to your bot with the text `/register` which will allow you to store the id in the application properties in the future.

--------------------------------------------------------------------------------
## Run application in screen
### Create new screen: `screen -S humanode-alert`
### Run application: `java -Dspring.config.location=/srv/humanode/application.properties -jar humanode-alert-1.0.0.jar`
### Send a message to your bot to register your chat id `/register`
### Exit from screen: `CTRL+A+D`

--------------------------------------------------------------------------------
## HELP
### Resume screen: `screen -r humanode-alert`

--------------------------------------------------------------------------------
## IMPORTANT 
### The application must be started on the same machine as the humanode.