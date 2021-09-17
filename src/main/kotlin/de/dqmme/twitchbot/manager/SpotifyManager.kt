package de.dqmme.twitchbot.manager

import com.google.gson.Gson
import com.google.gson.JsonObject
import de.dqmme.twitchbot.dataclass.SpotifyPlaylist
import de.dqmme.twitchbot.dataclass.SpotifySong
import de.dqmme.twitchbot.dataclass.SpotifyToken
import de.dqmme.twitchbot.util.Config
import de.dqmme.twitchbot.util.readJsonObjectFromFile
import okhttp3.*
import java.io.IOException

private val client = OkHttpClient()

//pause user playback
fun pausePlayback(callback: (Boolean) -> Unit) {
    val token = storedToken()

    if (token == null) {
        callback.invoke(false)
        return
    }

    val request = Request.Builder()
        .url("https://api.spotify.com/v1/me/player/pause")
        .addHeader("Accept", "application/json")
        .addHeader("Content-Type", "application/json")
        .addHeader("Authorization", "Bearer " + token.accessToken)
        .put(FormBody.Builder().build())
        .build()

    val call = client.newCall(request)

    call.enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            //request failed, return false
            callback.invoke(false)
        }

        override fun onResponse(call: Call, response: Response) {
            //request successful, return true
            callback.invoke(true)
        }
    })
}

//request the current playing playlist (name, author, URL)
fun requestCurrentPlaylist(callback: (SpotifyPlaylist?) -> Unit) {
    val token = storedToken()

    if (token == null) {
        callback.invoke(null)
        return
    }

    val request = Request.Builder()
        .url("https://api.spotify.com/v1/me/player/currently-playing")
        .addHeader("Accept", "application/json")
        .addHeader("Content-Type", "application/json")
        .addHeader("Authorization", "Bearer " + token.accessToken)
        .build()

    val call = client.newCall(request)

    call.enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            //request failed, return null
            callback.invoke(null)
        }

        override fun onResponse(call: Call, response: Response) {
            val responseString = response.body!!.string()

            val jsonObject = Gson().fromJson(responseString, JsonObject::class.java)

            val contextElement = jsonObject["context"]

            //check if a song is playing
            if (contextElement == null) {
                callback.invoke(null)
                return
            }

            val contextObject = contextElement.asJsonObject

            val type = contextObject["type"].asString

            //check if a playlist is playing
            if (type != "playlist") {
                callback.invoke(null)
                return
            }

            val externalUrlsObject = contextObject["external_urls"].asJsonObject

            //get playlist url
            val spotifyUrl = externalUrlsObject["spotify"].asString

            //get url to request more playlist details
            val apiUrl = contextObject["href"].asString

            //request more playlist details
            val playlistRequest = Request.Builder()
                .url(apiUrl)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + token.accessToken)
                .build()

            val playlistCall = client.newCall(playlistRequest)

            playlistCall.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    //request failed, return null
                    callback.invoke(null)
                }

                override fun onResponse(call: Call, response: Response) {
                    val playlistResponseString = response.body!!.string()

                    val playlistObject = Gson().fromJson(playlistResponseString, JsonObject::class.java)

                    //get name of the playlist
                    val name = playlistObject["name"].asString

                    val ownerObject = playlistObject["owner"].asJsonObject

                    //get owner of the playlist
                    val ownerName = ownerObject["display_name"].asString

                    callback.invoke(SpotifyPlaylist(name, ownerName, spotifyUrl))
                }
            })
        }
    })
}

//play the previous played song
fun previousSong(callback: (Boolean) -> Unit) {
    val token = storedToken()

    if (token == null) {
        callback.invoke(false)
        return
    }

    val request = Request.Builder()
        .url("https://api.spotify.com/v1/me/player/previous")
        .addHeader("Accept", "application/json")
        .addHeader("Content-Type", "application/json")
        .addHeader("Authorization", "Bearer " + token.accessToken)
        .post(FormBody.Builder().build())
        .build()

    val call = client.newCall(request)

    call.enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            //request failed, return false
            callback.invoke(false)
        }

        override fun onResponse(call: Call, response: Response) {
            //request successful, return true
            callback.invoke(true)
        }
    })
}

//add a song to queue
fun queueSong(songId: String, callback: (Boolean) -> Unit) {
    val token = storedToken()

    if (token == null) {
        callback.invoke(false)
        return
    }


    val request = Request.Builder()
        .url("https://api.spotify.com/v1/me/player/queue?uri=$songId")
        .addHeader("Accept", "application/json")
        .addHeader("Content-Type", "application/json")
        .addHeader("Authorization", "Bearer " + token.accessToken)
        .post(FormBody.Builder().build())
        .build()

    val call = client.newCall(request)

    call.enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            //request failed, return false
            callback.invoke(false)
        }

        override fun onResponse(call: Call, response: Response) {
            //request successful, return true
            callback.invoke(true)
        }
    })
}

//skip the current song
fun skipSong(callback: (Boolean) -> Unit) {
    val token = storedToken()

    if (token == null) {
        callback.invoke(false)
        return
    }


    val request = Request.Builder()
        .url("https://api.spotify.com/v1/me/player/next")
        .addHeader("Accept", "application/json")
        .addHeader("Content-Type", "application/json")
        .addHeader("Authorization", "Bearer " + token.accessToken)
        .post(FormBody.Builder().build())
        .build()

    val call = client.newCall(request)

    call.enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            //request failed, return false
            callback.invoke(false)
        }

        override fun onResponse(call: Call, response: Response) {
            //request successful, return true
            callback.invoke(true)
        }
    })
}

//request the current playing song (name, artist and URL)
fun requestCurrentSong(callback: (SpotifySong?) -> Unit) {
    val token = storedToken()

    if (token == null) {
        callback.invoke(null)
        return
    }

    val request = Request.Builder()
        .url("https://api.spotify.com/v1/me/player/currently-playing")
        .addHeader("Accept", "application/json")
        .addHeader("Content-Type", "application/json")
        .addHeader("Authorization", "Bearer " + token.accessToken)
        .build()

    val call = client.newCall(request)

    call.enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            callback.invoke(null)
        }

        override fun onResponse(call: Call, response: Response) {
            val responseString = response.body!!.string()

            val jsonObject = Gson().fromJson(responseString, JsonObject::class.java)

            val itemElement = jsonObject["item"]

            //check if a song is playing
            if (itemElement == null) {
                callback.invoke(null)
                return
            }

            val itemObject = itemElement.asJsonObject

            //get the song name
            val songName = itemObject["name"].asString

            val albumObject = itemObject["album"].asJsonObject

            val artistObject = albumObject["artists"].asJsonArray

            //get the artist
            val artistName = artistObject[0].asJsonObject["name"].asString

            val songUrlObject = itemObject["external_urls"].asJsonObject

            //get the song url
            val songUrl = songUrlObject["spotify"].asString

            callback.invoke(SpotifySong(songName, artistName, songUrl))
        }
    })
}

//start user playback
fun startPlayback(callback: (Boolean) -> Unit) {
    val token = storedToken()

    if (token == null) {
        callback.invoke(false)
        return
    }

    val request = Request.Builder()
        .url("https://api.spotify.com/v1/me/player/play")
        .addHeader("Accept", "application/json")
        .addHeader("Content-Type", "application/json")
        .addHeader("Authorization", "Bearer " + token.accessToken)
        .put(FormBody.Builder().build())
        .build()

    val call = client.newCall(request)

    call.enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            //request failed, return false
            callback.invoke(false)
        }

        override fun onResponse(call: Call, response: Response) {
            //request successful, return true
            callback.invoke(true)
        }
    })
}

//set current volume
fun setVolume(volume: Int, callback: (Boolean) -> Unit) {
    val token = storedToken()

    if (token == null) {
        callback.invoke(false)
        return
    }

    val request = Request.Builder()
        .url("https://api.spotify.com/v1/me/player/volume?volume_percent=$volume")
        .addHeader("Accept", "application/json")
        .addHeader("Content-Type", "application/json")
        .addHeader("Authorization", "Bearer " + token.accessToken)
        .put(FormBody.Builder().build())
        .build()

    val call = client.newCall(request)

    call.enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            //request failed, return false
            callback.invoke(false)
        }

        override fun onResponse(call: Call, response: Response) {
            //request successful, return true
            callback.invoke(true)
        }
    })
}

//request a new access + refresh token
fun requestSpotifyToken(code: String, callback: (SpotifyToken?) -> Unit) {
    //parameters to post
    val requestBody = FormBody.Builder()
        .add("client_id", Config.SPOTIFY_CLIENT_ID)
        .add("client_secret", Config.SPOTIFY_CLIENT_SECRET)
        .add("redirect_uri", Config.SPOTIFY_REDIRECT_URI)
        .add("grant_type", "authorization_code")
        .add("code", code)
        .build()

    val request = Request.Builder()
        .url("https://accounts.spotify.com/api/token")
        .post(requestBody)
        .build()

    val call = client.newCall(request)

    call.enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            //request failed, return null
            callback.invoke(null)
        }

        override fun onResponse(call: Call, response: Response) {
            val responseString = response.body!!.string()

            val jsonObject = Gson().fromJson(responseString, JsonObject::class.java)

            val accessToken = jsonObject["access_token"].asString
            val refreshToken = jsonObject["refresh_token"].asString

            //got tokens, return them
            callback.invoke(SpotifyToken(accessToken, refreshToken))
        }
    })
}

//request an access token with refresh Token
fun requestSpotifyToken(callback: (SpotifyToken?) -> Unit) {
    val token = storedToken()

    if (token == null) {
        callback.invoke(null)
        return
    }

    //parameters to post
    val requestBody = FormBody.Builder()
        .add("client_id", Config.SPOTIFY_CLIENT_ID)
        .add("client_secret", Config.SPOTIFY_CLIENT_SECRET)
        .add("redirect_uri", Config.SPOTIFY_REDIRECT_URI)
        .add("grant_type", "refresh_token")
        .add("refresh_token", token.refreshToken)
        .build()

    val request = Request.Builder()
        .url("https://accounts.spotify.com/api/token")
        .post(requestBody)
        .build()

    val call = client.newCall(request)

    call.enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            //request failed, return null
            callback.invoke(null)
        }

        override fun onResponse(call: Call, response: Response) {
            val responseString = response.body!!.string()

            val jsonObject = Gson().fromJson(responseString, JsonObject::class.java)

            val accessTokenElement = jsonObject["access_token"]

            if (accessTokenElement == null) {
                callback.invoke(null)
                return
            }

            val accessToken = accessTokenElement.asString
            val refreshToken = token.refreshToken

            //got access token, return it
            callback.invoke(SpotifyToken(accessToken, refreshToken))
        }
    })
}

//read stored spotify tokens
fun storedToken(): SpotifyToken? {
    val jsonObject = readJsonObjectFromFile("spotify-data.json")

    val accessTokenElement = jsonObject["access_token"] ?: return null
    val refreshTokenElement = jsonObject["refresh_token"] ?: return null

    val accessToken = accessTokenElement.asString
    val refreshToken = refreshTokenElement.asString

    return SpotifyToken(accessToken, refreshToken)
}