package org.pichugroup.pichuparkingapi

import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import org.pichugroup.thirdpartyparkingapi.ThirdPartyParkingAPI
import org.pichugroup.schema.PichuParkingAPIResponse
import org.pichugroup.schema.PichuParkingData
import java.time.LocalDateTime
import io.github.cdimascio.dotenv.dotenv
import io.ktor.utils.io.*
import org.pichugroup.thirdpartyparkingapi.LTAParkingAPI
import org.pichugroup.thirdpartyparkingapi.URAParkingAPI
import java.io.InputStream

val dotenv = dotenv {
    ignoreIfMissing=true
}

val URA_ACCESS_KEY: String = System.getenv("URA_ACCESS_KEY") ?: dotenv["URA_ACCESS_KEY"] ?: ""
val LTA_ACCOUNT_KEY: String = System.getenv("LTA_ACCOUNT_KEY") ?: dotenv["LTA_ACCOUNT_KEY"] ?: ""

class PichuParkingAPI(
    private val thirdPartyAPIs: Collection<ThirdPartyParkingAPI> = listOf(
    URAParkingAPI(accessKey = URA_ACCESS_KEY),
    LTAParkingAPI(accountKey = LTA_ACCOUNT_KEY))) {

    fun getParkingLots(apis: Collection<ThirdPartyParkingAPI> = thirdPartyAPIs): String {
        val currentTime = LocalDateTime.now().toString()

        val parkingLotData: MutableSet<PichuParkingData> = mutableSetOf()

        for (api in apis) {
            runBlocking {
                val data: Set<PichuParkingData> = api.getParkingLots()
                parkingLotData.addAll(data)
            }
        }

        val response = PichuParkingAPIResponse(timestamp = currentTime, data = parkingLotData)
        return convertToJson<PichuParkingAPIResponse>(response)
    }

    private inline fun <reified  T> convertToJson(dataClass: T): String {
        val gson = Gson()
        return gson.toJson(dataClass, T::class.java)
    }

}
