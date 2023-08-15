package org.pichugroup.pichuparkingapi

import kotlinx.coroutines.runBlocking
import org.pichugroup.thirdpartyparkingapi.ThirdPartyParkingAPI
import org.pichugroup.schema.PichuParkingAPIResponse
import org.pichugroup.schema.PichuParkingData
import java.time.LocalDateTime
import io.github.cdimascio.dotenv.dotenv
import org.pichugroup.thirdpartyparkingapi.LTAParkingAPI
import org.pichugroup.thirdpartyparkingapi.URAParkingAPI

val dotenv = dotenv()

val URA_ACCESS_KEY: String = System.getenv("URA_ACCESS_KEY") ?: dotenv["URA_ACCESS_KEY"] ?: ""
val LTA_ACCOUNT_KEY: String = System.getenv("LTA_ACCOUNT_KEY") ?: dotenv["LTA_ACCOUNT_KEY"] ?: ""

fun getParkingLots(parkingLotAPIs: Collection<ThirdPartyParkingAPI> = listOf(
    URAParkingAPI(accessKey = URA_ACCESS_KEY),
    LTAParkingAPI(accountKey = LTA_ACCOUNT_KEY),
)): PichuParkingAPIResponse {
    val currentTime = LocalDateTime.now().toString()

    val parkingLotData: Set<PichuParkingData> = mutableSetOf()

    for (api in parkingLotAPIs) {
        runBlocking {
            val data: Set<PichuParkingData> = api.getParkingLots()
            parkingLotData.union(data)
        }
    }

    return PichuParkingAPIResponse(timestamp = currentTime, data = parkingLotData)
}