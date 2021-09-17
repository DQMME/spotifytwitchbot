package de.dqmme.twitchbot.enum

enum class PermissionLevel(val level: Int) {
    BROADCASTER(2),
    MODERATOR(1),
    USER(0);
}