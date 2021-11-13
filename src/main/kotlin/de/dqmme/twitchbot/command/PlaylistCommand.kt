package de.dqmme.twitchbot.command

import com.github.philippheuer.events4j.simple.SimpleEventHandler
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import de.dqmme.twitchbot.manager.checkPermission
import de.dqmme.twitchbot.manager.command
import de.dqmme.twitchbot.manager.requestCurrentPlaylist
import de.dqmme.twitchbot.util.Config

class PlaylistCommand(eventHandler: SimpleEventHandler) {
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

        val playlistCommand = command("playlist") ?: return

        //check if the command is enabled
        if (!playlistCommand.enabled) return

        //check for the command
        if (command != playlistCommand.name.lowercase()) return

        checkPermission(
            event.channel.name,
            event.user.name
        ) { permissionLevel ->
            //check if user has permission
            if (permissionLevel.level < playlistCommand.permissionLevel.level) return@checkPermission

            //request current playlist
            requestCurrentPlaylist { spotifyPlaylist ->
                //no playlist is playing or an error occurred
                if (spotifyPlaylist != null) {
                    val message = playlistCommand.message
                        .replace("%name%", spotifyPlaylist.name)
                        .replace("%owner%", spotifyPlaylist.owner)
                        .replace("%link%", spotifyPlaylist.url)
                        .replace("%user%", event.user.name)

                    //send the current playlist to chat
                    event.twitchChat.sendMessage(
                        event.channel.name,
                        message
                    )
                }
            }
        }
    }
}