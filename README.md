## Configuration

--------------------------------------------------------------------------------
### Install openjdk-17-jdk: `sudo apt-get install openjdk-17-jdk`

--------------------------------------------------------------------------------
### Create directory: `mkdir /srv/humanode/`
### Go to directory: `cd /srv/humanode/`
### Clone jar from git: `git clone --branch build https://github.com/gicu-adasanu/humanode-alert.git`
### Go to directory: `cd humanode-alert`
### Edit application.properties and put your Telegram bot token: `bot.token=your_bot_token` and `chat.id=your_chat_id`

--------------------------------------------------------------------------------
## Run application in screen
### Create new screen: `screen -S humanode-alert`
### Run application: `java -Dspring.config.location=/srv/humanode/humanode-alert/application.properties -jar humanode-alert-1.0.0.jar`
### Exit from screen: `CTRL+A+D`

--------------------------------------------------------------------------------
## HELP
### Resume screen: `screen -r humanode-alert`

--------------------------------------------------------------------------------
## IMPORTANT 
### The application must be started on the same machine as the humanode.