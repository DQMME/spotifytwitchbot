package de.dqmme.twitchbot.util

import org.ini4j.Ini
import org.ini4j.IniPreferences
import java.io.File


object Config {
    val SPOTIFY_CLIENT_ID = fromIni("Spotify", "CLIENT_ID")
    val SPOTIFY_CLIENT_SECRET = fromIni("Spotify", "CLIENT_SECRET")
    val SPOTIFY_REDIRECT_URI = fromIni("Spotify", "REDIRECT_URI")

    val PORT = fromIni("Webserver", "PORT").toInt()

    val TWITCH_CLIENT_ID = fromIni("Twitch", "CLIENT_ID")
    val TWITCH_CLIENT_SECRET = fromIni("Twitch", "CLIENT_SECRET")
    val TWITCH_ACCESS_TOKEN = fromIni("Twitch", "ACCESS_TOKEN")
    val TWITCH_CHANNEL_NAME = fromIni("Twitch", "CHANNEL_NAME")

    val ENABLE_REWARD_SONG_REQUEST = fromIni("Reward", "ENABLE_REWARD_SONG_REQUEST").toBoolean()
    val REWARD_NAME = fromIni("Reward", "REWARD_NAME")

    private fun fromIni(section: String, key: String): String {
        val ini = Ini(File("config.ini"))
        val preferences = IniPreferences(ini)
        return preferences.node(section)[key, ""]
    }
}