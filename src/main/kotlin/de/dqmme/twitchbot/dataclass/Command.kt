package de.dqmme.twitchbot.dataclass

import de.dqmme.twitchbot.enum.PermissionLevel

data class Command(val name: String, val enabled: Boolean, val permissionLevel: PermissionLevel)
