package org.pichugroup.pichuparkingapi

import io.mockk.InternalPlatformDsl.toStr
import org.junit.jupiter.api.Test
import org.pichugroup.schema.PichuParkingAPIResponse
import org.pichugroup.thirdpartyparkingapi.LTAParkingAPI
import org.pichugroup.thirdpartyparkingapi.ThirdPartyParkingAPI
import org.pichugroup.thirdpartyparkingapi.URAParkingAPI
import java.time.LocalDate
import kotlin.test.assertEquals

class PichuParkingAPITest {
    @Test
    fun testGetParkingLot() {
        val currentDate: String = LocalDate.now().toStr()
        val response: PichuParkingAPIResponse = getParkingLots()
        val responseDateTime: String = response.timestamp
        val responseDate: String = responseDateTime.substring(0,10)
        assertEquals(currentDate, responseDate)
    }
}