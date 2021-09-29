package de.dqmme.twitchbot.listener

import com.github.philippheuer.events4j.simple.SimpleEventHandler
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent
import de.dqmme.twitchbot.manager.queueSong
import de.dqmme.twitchbot.util.Config

class ChannelPointsListener(eventHandler: SimpleEventHandler) {
    init {
        //listen to the Event
        eventHandler.onEvent(RewardRedeemedEvent::class.java, this::onRewardRedeem)
    }

    private fun onRewardRedeem(event: RewardRedeemedEvent) {
        if (event.redemption.reward.title.lowercase() != Config.REWARD_NAME.lowercase()) return

        val spotifySongId = spotifyIdFromText(event.redemption.userInput) ?: return

        queueSong(spotifySongId) {}
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