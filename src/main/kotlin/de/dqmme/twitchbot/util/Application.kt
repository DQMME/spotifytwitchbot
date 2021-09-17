package de.dqmme.twitchbot.util

import com.google.gson.JsonObject
import de.dqmme.twitchbot.manager.requestSpotifyToken
import io.ktor.application.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.routing.*
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.button

fun Application.configureRouting() {
    //spotify page with button
    routing {
        get("/spotify") {
            call.respondHtml(HttpStatusCode.OK) {
                body {
                    val spotifyUrl = "https://accounts.spotify.com/authorize" +
                            "?response_type=code" +
                            "&client_id=${Config.SPOTIFY_CLIENT_ID}" +
                            "&scope=user-modify-playback-state%20user-read-currently-playing" +
                            "&redirect_uri=${Config.SPOTIFY_REDIRECT_URI}"
                    a(spotifyUrl) {
                        button {
                            +"Click here"
                        }
                    }
                }
            }
        }

        //callback page
        get("/spotifyCallback") {
            val code = call.request.uri
                .split("?")[1]
                .replace("code=", "")

            call.respondHtml(HttpStatusCode.OK) {}
            requestSpotifyToken(code) { spotifyToken ->
                val jsonObject = JsonObject()

                jsonObject.addProperty("access_token", spotifyToken!!.accessToken)
                jsonObject.addProperty("refresh_token", spotifyToken.refreshToken)

                writeFile("spotify-data.json", jsonObject.toString())

                println("Tokens saved. Restart the Bot now!")
            }
        }
    }
}