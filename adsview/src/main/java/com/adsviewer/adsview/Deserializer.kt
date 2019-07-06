package com.adsviewer.adsview

import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

open class Deserializer<T> : JsonDeserializer<T> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): T {
        val content = json!!.asJsonObject

        // Deserialize it. You use a new instance of Gson to avoid infinite recursion to this deserializer
        return Gson().fromJson(content, typeOfT)
    }
}