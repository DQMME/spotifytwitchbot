package de.dqmme.twitchbot.command

import com.github.philippheuer.events4j.simple.SimpleEventHandler
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import de.dqmme.twitchbot.manager.checkPermission
import de.dqmme.twitchbot.manager.command
import de.dqmme.twitchbot.manager.setVolume

class VolumeCommand(eventHandler: SimpleEventHandler) {
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

        val volumeCommand = command("volume") ?: return

        //check if the command is enabled
        if (!volumeCommand.enabled) return

        //check for the command
        if (command != volumeCommand.usage.lowercase()) return

        if (args.size < 1) {
            event.twitchChat.sendMessage(event.channel.name, "Falscher Syntax! Verwendung: !volume <1-100>")
            return
        }

        checkPermission(
            event.channel.name,
            event.user.name
        ) { permissionLevel ->
            //check if user has permission
            if (permissionLevel.level < volumeCommand.permissionLevel.level) return@checkPermission

            val volume = args[0]
                .replace("%", "")
                .toIntOrNull()

            if (volume == null) {
                event.twitchChat.sendMessage(event.channel.name, "Du musst eine Zahl angeben! @${event.user.name}")
                return@checkPermission
            }

            if (volume > 100 || volume < 1) {
                event.twitchChat.sendMessage(
                    event.channel.name,
                    "Du musst eine Zahl zwischen 1 und 100 angeben! @${event.user.name}"
                )
                return@checkPermission
            }

            //set current volume
            setVolume(volume) { successful ->
                //request was successful
                if (successful) {
                    val message = volumeCommand.message
                        .replace("%volume%", "$volume%")
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