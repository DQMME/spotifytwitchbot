package de.dqmme.twitchbot

import com.google.gson.JsonObject
import de.dqmme.twitchbot.dataclass.SpotifyToken
import de.dqmme.twitchbot.manager.requestSpotifyToken
import de.dqmme.twitchbot.util.Config
import de.dqmme.twitchbot.util.configureRouting
import de.dqmme.twitchbot.util.writeFile
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.util.*

fun main() {
    //try to request access token with the refresh Token
    requestSpotifyToken { spotifyToken ->
        if (spotifyToken == null) {
            //cannot get Access Token
            println("Access Token cannot be request. Make sure to follow the Steps on bla")

            //start ktor webserver for spotify authorization
            startKtor()
        } else {
            saveSpotifyToken(spotifyToken)

            //start the twitch Bot
            val bot = Bot
            bot.start()

            //request every 30 minutes new access token
            val timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    requestSpotifyToken { spotifyToken ->
                        saveSpotifyToken(spotifyToken!!)
                    }
                }
            }, 0, 1800000L)
        }
    }
}

private fun startKtor() {
    embeddedServer(Netty, port = Config.PORT, host = "0.0.0.0") {
        configureRouting()
    }.start(wait = true)
}

private fun saveSpotifyToken(spotifyToken: SpotifyToken) {
    //creating a new json object
    val jsonObject = JsonObject()

    //adding tokens to the object
    jsonObject.addProperty("access_token", spotifyToken.accessToken)
    jsonObject.addProperty("refresh_token", spotifyToken.refreshToken)

    //save it
    writeFile("spotify-data.json", jsonObject.toString())
}