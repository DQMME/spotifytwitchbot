package de.dqmme.twitchbot.util

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.stream.JsonReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter

fun readJsonObjectFromFile(fileName: String): JsonObject {
    val file = File(fileName)

    if (!file.exists()) {
        file.createNewFile()

        writeFile(fileName, "{}")
    }

    val gson = Gson()

    val reader = JsonReader(FileReader(fileName))

    return gson.fromJson(reader, JsonObject::class.java)
}

fun writeFile(fileName: String, toWrite: String) {
    val file = File(fileName)

    if (!file.exists()) {
        file.createNewFile()
    }

    val fileWriter = FileWriter(fileName)

    fileWriter.write(toWrite)

    fileWriter.close()

}