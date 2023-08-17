package org.pichugroup.thirdpartyparkingapi

import com.google.gson.Gson

internal fun textJsonToMap(text: String): Map<String, Any> {
    val gson = Gson()
    val jsonMap = gson.fromJson(text, Map::class.java)
    val resultMap = mutableMapOf<String, Any>()

    for ((key, value) in jsonMap) {
        val keyString: String = key.toString()
        resultMap[keyString] = value as Any
    }

    return resultMap.toMap()
}
