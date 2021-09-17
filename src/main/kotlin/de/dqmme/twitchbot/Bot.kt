package de.dqmme.twitchbot

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential
import com.github.philippheuer.events4j.simple.SimpleEventHandler
import com.github.twitch4j.TwitchClient
import com.github.twitch4j.TwitchClientBuilder
import com.google.gson.Gson
import com.google.gson.JsonObject
import de.dqmme.twitchbot.command.*
import de.dqmme.twitchbot.listener.ChannelPointsListener
import de.dqmme.twitchbot.util.Config
import okhttp3.*
import java.io.IOException

object Bot {
    private val twitchClient: TwitchClient = createClient()

    private fun registerListeners() {
        val eventHandler = twitchClient.eventManager.getEventHandler(SimpleEventHandler::class.java)

        //register every Command
        PauseCommand(eventHandler)
        PlaylistCommand(eventHandler)
        PreviousCommand(eventHandler)
        QueueCommand(eventHandler)
        SkipCommand(eventHandler)
        SongCommand(eventHandler)
        StartCommand(eventHandler)
        VolumeCommand(eventHandler)

        if (Config.ENABLE_REWARD_SONG_REQUEST) {
            ChannelPointsListener(eventHandler)
        }
    }

    fun start() {
        if(Config.ENABLE_REWARD_SONG_REQUEST) {
            //listen for channel point redemptions
            channelIdByName(Config.TWITCH_CHANNEL_NAME) { channelId ->
                twitchClient.pubSub.listenForChannelPointsRedemptionEvents(
                    OAuth2Credential(
                        "twitch",
                        Config.TWITCH_ACCESS_TOKEN
                    ), channelId.toString()
                )
            }
        }

        registerListeners()

        //let the bot join the channel
        twitchClient.chat.joinChannel(Config.TWITCH_CHANNEL_NAME)
    }

    private fun createClient(): TwitchClient {
        var clientBuilder = TwitchClientBuilder.builder()

        //create new auth credential
        val credential = OAuth2Credential(
            "twitch",
            Config.TWITCH_ACCESS_TOKEN
        )

        clientBuilder = clientBuilder
            .withChatAccount(credential)
            .withEnableChat(true)


        clientBuilder = if (Config.ENABLE_REWARD_SONG_REQUEST) {
            clientBuilder
                .withClientId(Config.TWITCH_CLIENT_ID)
                .withClientSecret(Config.TWITCH_CLIENT_SECRET)
                .withEnableHelix(true)
                .withEnableKraken(true)
                .withEnablePubSub(true)
        } else {
            clientBuilder
                .withClientId(Config.TWITCH_CLIENT_ID)
                .withClientSecret(Config.TWITCH_CLIENT_SECRET)
                .withEnableHelix(true)
                .withEnableKraken(true)
                .withEnablePubSub(true)
        }

        return clientBuilder.build()
    }

    private fun channelIdByName(name: String, callback: (Int?) -> Unit) {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://api.twitch.tv/helix/users?login=$name")
            .addHeader("Authorization", "Bearer " + Config.TWITCH_ACCESS_TOKEN)
            .addHeader("Client-Id", Config.TWITCH_CLIENT_ID)
            .build()

        val call = client.newCall(request)

        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                //request failed, return null
                callback.invoke(null)
            }

            override fun onResponse(call: Call, response: Response) {
                //request successful, return channel id
                val responseString = response.body!!.string()

                val jsonObject = Gson().fromJson(responseString, JsonObject::class.java)

                val dataElement = jsonObject["data"]

                //check if data is given
                if (dataElement == null) {
                    callback.invoke(null)
                    return
                }

                val dataArray = dataElement.asJsonArray

                val channelId = dataArray[0].asJsonObject["id"].asInt

                callback.invoke(channelId)
            }
        })
    }
}