package de.dqmme.twitchbot.command

import com.github.philippheuer.events4j.simple.SimpleEventHandler
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import de.dqmme.twitchbot.manager.checkPermission
import de.dqmme.twitchbot.manager.command
import de.dqmme.twitchbot.manager.startPlayback

class StartCommand(eventHandler: SimpleEventHandler) {
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

        val startCommand = command("start") ?: return

        //check if the command is enabled
        if (!startCommand.enabled) return

        //check for the command
        if (command != startCommand.name.lowercase()) return

        checkPermission(
            event.channel.name,
            event.user.name
        ) { permissionLevel ->
            //check if user has permission
            if (permissionLevel.level < startCommand.permissionLevel.level) return@checkPermission

            //start current playback
            startPlayback { successful ->
                //request was successful
                if (successful) {
                    event.twitchChat.sendMessage(
                        event.channel.name,
                        "Du hast die Wiedergabe erfolgreich fortgesetzt. @${event.user.name}"
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
}