package org.pichugroup.pichuparkingapi

import com.google.gson.Gson
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.runBlocking
import org.pichugroup.schema.PichuParkingAPIResponse
import org.pichugroup.schema.PichuParkingLots
import org.pichugroup.schema.PichuParkingRates
import org.pichugroup.thirdpartyparkingapi.*
import java.time.LocalDateTime

private val dotenv = dotenv {
    ignoreIfMissing = true
}

private val uraAccessKey: String = System.getenv("URA_ACCESS_KEY") ?: dotenv["URA_ACCESS_KEY"] ?: ""
private val ltaAccountKey: String = System.getenv("LTA_ACCOUNT_KEY") ?: dotenv["LTA_ACCOUNT_KEY"] ?: ""

private val apisToUse: Set<ThirdPartyParkingAPI> = getAPIsToUse() ?: ThirdPartyParkingAPI.all

private val apiFactoryMapping: Map<ThirdPartyParkingAPI, Pair<String, ThirdPartyParkingAPIClientFactory>> = mapOf(
    ThirdPartyParkingAPI.LTA to Pair(ltaAccountKey, LTAParkingAPIClientFactory),
    ThirdPartyParkingAPI.URA to Pair(uraAccessKey, URAParkingAPIClientFactory),
)

private fun getAPIsToUse(): Set<ThirdPartyParkingAPI>? {
    val apisToUseString: List<String>? = getAPIsToUseFromEnvironment()
    return apisToUseString?.map { apiString ->
        ThirdPartyParkingAPI.fromDescription(apiString)
    }?.toSet()
}

private fun getAPIsToUseFromEnvironment(): List<String>? {
    return System.getenv("APIS_TO_USE")?.split(",")
}

class PichuParkingAPI(private val thirdPartyAPIsToUse: Set<ThirdPartyParkingAPI> = apisToUse) {
    private val instantiatedAPIs = instantiateAPIs()
    private fun instantiateAPIs(): List<ThirdPartyParkingAPIClient> {
        val instantiatedAPIs: MutableList<ThirdPartyParkingAPIClient> = mutableListOf()
        for (selectedAPI in thirdPartyAPIsToUse) {
            val (apiKey: String, apiFactory: ThirdPartyParkingAPIClientFactory) = apiFactoryMapping[selectedAPI]
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

    fun getParkingLots(): PichuParkingAPIResponse {
        val currentTime = LocalDateTime.now().toString()

        val parkingLotData: MutableSet<PichuParkingLots> = mutableSetOf()

        for (api in instantiatedAPIs) {
            runBlocking {
                val data: Set<PichuParkingLots> = api.getParkingLots()
                parkingLotData.addAll(data)
            }
        }

        return PichuParkingAPIResponse(timestamp = currentTime, data = parkingLotData)
    }

    fun getParkingRates(): PichuParkingAPIResponse {
        val currentTime = LocalDateTime.now().toString()
        val parkingRatesData: MutableSet<PichuParkingRates> = mutableSetOf()

        for (api in instantiatedAPIs) {
            runBlocking {
                val data: Set<PichuParkingRates> = api.getParkingRates()
                parkingRatesData.addAll(data)
            }
        }
        return PichuParkingAPIResponse(timestamp = currentTime, data = parkingRatesData)
    }

}

private inline fun <reified T> convertToJson(dataClass: T): String {
    val gson = Gson()
    return gson.toJson(dataClass, T::class.java)
}


