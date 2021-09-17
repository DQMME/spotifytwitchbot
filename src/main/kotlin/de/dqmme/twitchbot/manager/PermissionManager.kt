package de.dqmme.twitchbot.manager

import com.google.gson.Gson
import com.google.gson.JsonObject
import de.dqmme.twitchbot.enum.PermissionLevel
import okhttp3.*
import java.io.IOException

private val client = OkHttpClient()

fun checkPermission(channelName: String, userName: String, callback: (PermissionLevel) -> Unit) {
    modsOfChannel(channelName) { moderatorList ->
        //check if the user is the broadcaster
        if (channelName.lowercase() == userName.lowercase()) {
            callback.invoke(PermissionLevel.BROADCASTER)
            return@modsOfChannel
        }

        //check if the requested list contains the username
        if (moderatorList.contains(userName.lowercase())) {
            callback.invoke(PermissionLevel.MODERATOR)
            return@modsOfChannel
        }

        //user is not a moderator or the broadcaster
        callback.invoke(PermissionLevel.USER)
        return@modsOfChannel
    }
}

private fun modsOfChannel(channelName: String, callback: (List<String>) -> Unit) {
    val request: Request = Request.Builder()
        .url("https://tmi.twitch.tv/group/user/" + channelName.lowercase() + "/chatters")
        .build()

    val call: Call = client.newCall(request)
    call.enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            //request failed, return empty list
            callback.invoke(listOf())
        }

        override fun onResponse(call: Call, response: Response) {
            val responseString = response.body!!.string()

            val jsonObject = Gson().fromJson(responseString, JsonObject::class.java)

            //create moderator list
            val moderatorList = arrayListOf<String>()

            //add moderators to it
            val chattersObject = jsonObject["chatters"].asJsonObject
            val moderatorsArray = chattersObject["moderators"].asJsonArray
            for (jsonElement in moderatorsArray) {
                val name = jsonElement.asString
                moderatorList.add(name)
            }

            //return it
            callback.invoke(moderatorList)
        }
    })
}