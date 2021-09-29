package de.dqmme.twitchbot.command

import com.github.philippheuer.events4j.simple.SimpleEventHandler
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import de.dqmme.twitchbot.manager.checkPermission
import de.dqmme.twitchbot.manager.command
import de.dqmme.twitchbot.manager.pausePlayback

class PauseCommand(eventHandler: SimpleEventHandler) {
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

        val pauseCommand = command("pause") ?: return

        //check if the command is enabled
        if (!pauseCommand.enabled) return

        //check for the command
        if (command != pauseCommand.name.lowercase()) return

        checkPermission(
            event.channel.name,
            event.user.name
        ) { permissionLevel ->
            //check if user has permission
            if (permissionLevel.level < pauseCommand.permissionLevel.level) return@checkPermission

            //pause current playback
            pausePlayback { successful ->
                //request was successful
                if (successful) {
                    val message = pauseCommand.message
                        .replace("%user%", event.user.name)

                    event.twitchChat.sendMessage(
                        event.channel.name,
                        message
                    )
                }
            }
        }
    }
}