# Spotify Twitch Bot

#### A simple Twitch Bot to handle Spotify Requests

## Features

- **Customizable Command Usage**
- **Customizable Command Messages**
- **Customizable Command Permission (User, Moderator, Broadcaster)**
- **En/Disable Commands**
- **Async**
- **Add Song to Queue via Channel Point Reward**
- **Pause Spotify Playback via Command**
- **Start Spotify Playback via Command**
- **View Current Song via Command (Name, Artist, URL)**
- **View Current Playlist via Command (Name, Owner, URL)**
- **Skip Song via Command**
- **Play previous Song via Command**
- **Set Volume via Command**
- **Add Song to Queue via Command**

## Requirements

- [**Java 11 or higher**](https://adoptopenjdk.net/)
- [**Spotify Account (Free or Premium)**](https://www.spotify.com/)
- [**Twitch Account**](https://twitch.tv)

## Setup

### Create Spotify Applicaton

#### Go to your [Spotify Developer Dashboard](https://developer.spotify.com/dashboard/) and log into your Account (if you aren´t already).

#### Click on ***Create an App***.

![](https://i.imgur.com/zUQpKiql.png)

#### Enter App Name and Description and accept the ToS and Guidelines.

#### For this Tutorial we`ll be using the Name *Spotify Twitch Bot* .

#### Click on *Create*.

![](https://i.imgur.com/fBNcfQ5m.png)

#### Click on *Show Client Secret*.

![](https://i.imgur.com/Yk78w0Hm.png)

#### Make a note of your Client ID and Client Secret in a text document or something similar, you will need them later.

#### Next click on *Edit Settings*.

![](https://i.imgur.com/H1GXyEWm.png)

#### Enter your Redirect URI at *Redirect URIs* an hit *Add*  **(It needs to end with /spotifyCallback)**.

#### Make a note of your Redirect URI too.

#### For this Tutorial we`ll be using *http://localhost:8000/spotifyCallback* as Redirect URI.

![](https://i.imgur.com/3NwVShyl.png)

#### Scroll down a little bit and hit *Save*.

![](https://i.imgur.com/wAM3oR1m.png)

### Create Twitch Application

#### Go to your [Twitch Developer Console](https://dev.twitch.tv/console/apps) and log into your Account (if you aren`t already).

#### Click on *Register Your Application*.

![](https://i.imgur.com/ZlRtF1kh.png)

#### As Name we enter *Spotify Bot* again, but it doesn`t matter what name your application has.

#### For this Bot, you don`t need a redirect URI at Twitch so we can enter something like https://example.com ad hit *

Add*.

#### As Category you choose *Chat Bot*.

#### Hit *Create*.

![](https://i.imgur.com/6VwKVcZl.png)

#### Go to your Applications page again,find the application you just created and click Manage.

#### At the Bottom you click the button called *New Secret*.

![](https://i.imgur.com/cItm1S0l.png)

#### Make a Note of this Client Secret too.

### Generate Twitch Token

#### Head over to https://twitchtokengenerator.com.

#### A popup with 2 options will appear. You click on *Custom Scope Token*.

#### This Bot need the scopes *"chat : read"*, *"chat : edit"* and *"channel : read : redemptions"*.

#### Hit *Generate Token!*.

![](https://i.imgur.com/7cUbSHk.png)

#### After Authorizing and completing the captcha, you see three fields with Tokens.

![](https://i.imgur.com/ZipRKBL.png)

#### Make Notes of you Access Token and your Client ID.

### Setup the Bot

#### Download the latest Version from [Releases](https://github.com/DQMME/spotifytwitchbot/releases/).

#### Unpack all the Files in a new Folder.

#### Now you need to fill in all the Data, you noticed.

#### Open the config.ini File and put in the Data you noticed.

<details>
  <summary>Config File</summary>

   ```ini
   [Spotify]
   CLIENT_ID = YOUR_SPOTIFY_CLIENT_ID
   CLIENT_SECRET = YOUR_SPOTIFY_CLIENT_SECRET
   REDIRECT_URI = YOUR_SPOTIFY_REDIRECT_URI

   [Webserver]
   PORT = 8000

   [Twitch]
   CLIENT_ID = YOUR_TWITCH_CLIENT_ID
   CLIENT_SECRET = YOUR_TWITCH_CLIENT_SECRET
   ACCESS_TOKEN = YOUR_TWITCH_ACCESS_TOKEN
   CHANNEL_NAME = YOUR_TWITCH_CHANNEL_NAME

   [Reward]
   ENABLE_REWARD_SONG_REQUEST = true
   REWARD_NAME = YOUR_SONG_REQUEST_REWARD_NAME
   ```

</details>

#### If you want to edit any of the Commands, you can edit the commands.json File.

<details>
  <summary>Commands FIle</summary>

   ```json
   {
  "pause": {
    "usage": "!pause",
    "enabled": true,
    "message": "Playback was paused successfully. @%user%",
    "permission": "MODERATOR"
  },
  "playlist": {
    "usage": "!playlist",
    "enabled": true,
    "message": "The Playlist %name% from %owner% is currently running. (\"%link%\") @%user%",
    "permission": "EVERYONE"
  },
  "previous": {
    "usage": "!previous",
    "enabled": true,
    "message": "The previous Song was played successfully. @%user%",
    "permission": "MODERATOR"
  },
  "queue": {
    "usage": "!queue",
    "enabled": true,
    "message": "The Song was successfully added to the queue. @%user%",
    "permission": "MODERATOR"
  },
  "skip": {
    "usage": "!skip",
    "enabled": true,
    "message": "The next Song was played successfully. @%user%",
    "permission": "MODERATOR"
  },
  "song": {
    "usage": "!song",
    "enabled": true,
    "message": "%name% by %artist% is currently running. (\"%link%\") @%user%",
    "permission": "EVERYONE"
  },
  "start": {
    "usage": "!start",
    "enabled": true,
    "message": "Playback was resumed successfully. @%user%",
    "permission": "MODERATOR"
  },
  "volume": {
    "usage": "!volume",
    "enabled": true,
    "message": "The volume was successfully set to %volume%. @%user%",
    "permission": "MODERATOR"
  }
}
   ```

</details>

#### Click your Start File (Windows or Linux) and go to *localhost:
PORT/spotify* The Port is your Port defined in the Config File (default = 8000).

#### Click the Button called *Click here*.

#### Authorize Spotify.

#### Now you can restart the Bot and you can see if the Access Token got requested.

### Finished!

## Credits

#### This Bot is using [Twitch4J](https://github.com/twitch4j/twitch4j), [Ini4J](https://mvnrepository.com/artifact/org.ini4j/ini4j), [Gson](https://github.com/google/gson), [OkHttp](https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp) and [Ktor](https://ktor.io/)
