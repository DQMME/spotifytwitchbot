package de.dqmme.twitchbot.command

import com.github.philippheuer.events4j.simple.SimpleEventHandler
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import de.dqmme.twitchbot.manager.checkPermission
import de.dqmme.twitchbot.manager.command
import de.dqmme.twitchbot.manager.queueSong

class QueueCommand(eventHandler: SimpleEventHandler) {
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

        val queueCommand = command("queue") ?: return

        //check if the command is enabled
        if (!queueCommand.enabled) return

        //check for the command
        if (command != queueCommand.name.lowercase()) return

        if (args.size < 1) {
            event.twitchChat.sendMessage(event.channel.name, "Falscher Syntax! Verwendung: !queue <songurl>")
            return
        }

        checkPermission(
            event.channel.name,
            event.user.name
        ) { permissionLevel ->
            //check if user has permission
            if (permissionLevel.level < queueCommand.permissionLevel.level) return@checkPermission

            val spotifyId = spotifyIdFromText(args[0])

            if (spotifyId == null) {
                event.twitchChat.sendMessage(event.channel.name, "Du musst einen Spotify Link angeben.+")
                return@checkPermission
            }

            //queue song
            queueSong(spotifyId) { successful ->
                //request was successful
                if (successful) {
                    event.twitchChat.sendMessage(
                        event.channel.name,
                        "Du den Song erfolgreich der Warteschlage hinzugefügt. @${event.user.name}"
                    )
                    //request wasn't successful
                } else {
                    event.twitchChat.sendMessage(
                        event.channel.name,
                        "Das hat nicht geklappt. @${event.user.name}"
                    )
                }
            }
        }
    }

    private fun spotifyIdFromText(text: String): String? {
        val spotifyUrlRegex =
            Regex("https?:\\/\\/(www\\.)?open.spotify{1,256}\\.com/track{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)")

        val matchResult = spotifyUrlRegex.find(text) ?: return null

        val spotifyUrlId = matchResult.value
            .split("?")[0]
            .replace("https", "")
            .replace("http", "")
            .replace("://", "")
            .replace("open.spotify.com", "")
            .replace("open.spotify.com", "")
            .replace("/", "")
            .replace("track", "")

        return "spotify:track:$spotifyUrlId"
    }
}