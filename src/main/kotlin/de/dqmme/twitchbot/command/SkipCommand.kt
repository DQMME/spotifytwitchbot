package de.dqmme.twitchbot.command

import com.github.philippheuer.events4j.simple.SimpleEventHandler
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import de.dqmme.twitchbot.manager.checkPermission
import de.dqmme.twitchbot.manager.command
import de.dqmme.twitchbot.manager.skipSong

class SkipCommand(eventHandler: SimpleEventHandler) {
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

        val skipCommand = command("skip") ?: return

        //check if the command is enabled
        if (!skipCommand.enabled) return

        //check for the command
        if (command != skipCommand.name.lowercase()) return

        checkPermission(
            event.channel.name,
            event.user.name
        ) { permissionLevel ->
            //check if user has permission
            if (permissionLevel.level < skipCommand.permissionLevel.level) return@checkPermission

            //skip current song
            skipSong { successful ->
                if (successful) {
                    event.twitchChat.sendMessage(
                        event.channel.name,
                        "Du hast den aktuellen Song erfolgreich übersprungen. @${event.user.name}"
                    )
                } else {
                    event.twitchChat.sendMessage(event.channel.name, "Da hat etwas nicht geklappt. @${event.user.name}")
                }
            }
        }
    }
}