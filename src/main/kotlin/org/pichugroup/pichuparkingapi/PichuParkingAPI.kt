package org.pichugroup.pichuparkingapi

import com.google.gson.Gson
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.runBlocking
import org.pichugroup.schema.PichuParkingAPIResponse
import org.pichugroup.schema.PichuParkingData
import org.pichugroup.thirdpartyparkingapi.LTAParkingAPIFactory
import org.pichugroup.thirdpartyparkingapi.ThirdPartyParkingAPI
import org.pichugroup.thirdpartyparkingapi.ThirdPartyParkingAPIFactory
import org.pichugroup.thirdpartyparkingapi.URAParkingAPIFactory
import java.time.LocalDateTime

private val dotenv = dotenv {
    ignoreIfMissing = true
}

private val uraAccessKey: String = System.getenv("URA_ACCESS_KEY") ?: dotenv["URA_ACCESS_KEY"] ?: ""
private val ltaAccountKey: String = System.getenv("LTA_ACCOUNT_KEY") ?: dotenv["LTA_ACCOUNT_KEY"] ?: ""

private val apisToUse: List<String> = (System.getenv("APIS_TO_USE") ?: "lta,ura").split(",")

private val apiFactoryMapping: Map<String, Pair<String, ThirdPartyParkingAPIFactory>> = mutableMapOf(
    "lta" to Pair(ltaAccountKey, LTAParkingAPIFactory),
    "ura" to Pair(uraAccessKey, URAParkingAPIFactory),
)

private fun instantiateAPIs(): List<ThirdPartyParkingAPI> {
    val instantiatedAPIs: MutableList<ThirdPartyParkingAPI> = mutableListOf()
    for (selectedAPI in apisToUse) {
        val (apiKey: String, apiFactory: ThirdPartyParkingAPIFactory) = apiFactoryMapping[selectedAPI]
            ?: throw Exception("API mapping not found")
        try {
            val apiInstance = apiFactory.createInstance(apiKey = apiKey)
            instantiatedAPIs.add(apiInstance)
        } catch (e: Exception) {
            println("Failed to instantiate $selectedAPI API: ${e.message}")
        }
    }
    return instantiatedAPIs
}

fun getParkingLots(thirdPartyAPIs: Collection<ThirdPartyParkingAPI> = instantiateAPIs()): PichuParkingAPIResponse {
    val currentTime = LocalDateTime.now().toString()

    val parkingLotData: MutableSet<PichuParkingData> = mutableSetOf()

    for (api in thirdPartyAPIs) {
        runBlocking {
            val data: Set<PichuParkingData> = api.getParkingLots()
            parkingLotData.addAll(data)
        }
    }

    return PichuParkingAPIResponse(timestamp = currentTime, data = parkingLotData)
}

private inline fun <reified T> convertToJson(dataClass: T): String {
    val gson = Gson()
    return gson.toJson(dataClass, T::class.java)
}


