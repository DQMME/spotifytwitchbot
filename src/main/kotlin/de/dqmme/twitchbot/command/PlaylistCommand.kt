package de.dqmme.twitchbot.command

import com.github.philippheuer.events4j.simple.SimpleEventHandler
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import de.dqmme.twitchbot.manager.checkPermission
import de.dqmme.twitchbot.manager.command
import de.dqmme.twitchbot.manager.requestCurrentPlaylist

class PlaylistCommand(eventHandler: SimpleEventHandler) {
    init {
        //listen to the Event
        eventHandler.onEvent(ChannelMessageEvent::class.java, this::onChannelMessage)
    }

    private fun onChannelMessage(event: ChannelMessageEvent) {
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
                if (spotifyPlaylist == null) {
                    event.twitchChat.sendMessage(event.channel.name, "Gerade läuft keine Playlist. @${event.user.name}")
                    return@requestCurrentPlaylist
                } else {
                    //send the current playlist to chat
                    event.twitchChat.sendMessage(
                        event.channel.name,
                        "Gerade läuft die Playlist ${spotifyPlaylist.name} von ${spotifyPlaylist.owner}. ${spotifyPlaylist.url} @${event.user.name}"
                    )
                }
            }
        }
    }
}