package de.dqmme.twitchbot.command

import com.github.philippheuer.events4j.simple.SimpleEventHandler
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import de.dqmme.twitchbot.manager.checkPermission
import de.dqmme.twitchbot.manager.command
import de.dqmme.twitchbot.manager.requestCurrentSong
import de.dqmme.twitchbot.util.Config

class SongCommand(eventHandler: SimpleEventHandler) {
    init {
        //listen to the Event
        eventHandler.onEvent(ChannelMessageEvent::class.java, this::onChannelMessage)
    }

    private fun onChannelMessage(event: ChannelMessageEvent) {
        if (event.channel.name.lowercase() != Config.TWITCH_CHANNEL_NAME.lowercase()) return

        val args = event.message
            .split(" ")
            .toMutableList()

        val command = args[0].lowercase()

        args.remove(args[0])

        val songCommand = command("song") ?: return

        //check if the command is enabled
        if (!songCommand.enabled) return

        //check for the command
        if (command != songCommand.name.lowercase()) return

        checkPermission(
            event.channel.name,
            event.user.name
        ) { permissionLevel ->
            //check if user has permission
            if (permissionLevel.level < songCommand.permissionLevel.level) return@checkPermission

            //request current song
            requestCurrentSong { spotifySong ->
                if (spotifySong != null) {
                    val message = songCommand.message
                        .replace("%name%", spotifySong.name)
                        .replace("%artist%", spotifySong.artist)
                        .replace("%link%", spotifySong.songUrl)
                        .replace("%user%", event.user.name)

                    //send the current song to chat
                    event.twitchChat.sendMessage(
                        event.channel.name,
                        message
                    )
                }
            }
        }
    }
}