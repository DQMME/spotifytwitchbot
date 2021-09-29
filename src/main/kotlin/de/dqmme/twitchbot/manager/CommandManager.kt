package de.dqmme.twitchbot.manager

import de.dqmme.twitchbot.dataclass.Command
import de.dqmme.twitchbot.enum.PermissionLevel
import de.dqmme.twitchbot.util.readJsonObjectFromFile

fun command(commandName: String): Command? {
    val commandFileObject = readJsonObjectFromFile("commands.json")

    val commandElement = commandFileObject.get(commandName.lowercase()) ?: return null

    val commandObject = commandElement.asJsonObject

    val usageElement = commandObject.get("usage") ?: return null

    val usage = usageElement.asString

    val enabledElement = commandObject.get("enabled") ?: return null

    val enabled = enabledElement.asBoolean

    val messageElement = commandObject.get("message") ?: return null

    val message = messageElement.asString

    val permissionLevelElement = commandObject.get("permission") ?: return null

    val permissionLevel = parsePermissionLevel(permissionLevelElement.asString)

    return Command(usage, enabled, message, permissionLevel)
}

private fun parsePermissionLevel(name: String): PermissionLevel {
    return if (name.lowercase() == "broadcaster") PermissionLevel.BROADCASTER
    else if (name.lowercase() == "moderator" || name.lowercase() == "mod") PermissionLevel.MODERATOR
    else PermissionLevel.USER
}