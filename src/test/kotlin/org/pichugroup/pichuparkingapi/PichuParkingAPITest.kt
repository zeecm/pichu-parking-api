package org.pichugroup.pichuparkingapi

import io.mockk.InternalPlatformDsl.toStr
import org.junit.jupiter.api.Test
import org.pichugroup.schema.PichuParkingAPIResponse
import java.time.LocalDate
import kotlin.test.assertEquals

class PichuParkingAPITest {
    private val api = PichuParkingAPI()
    @Test
    fun testGetParkingLot() {
        val currentDate: String = LocalDate.now().toStr()
        val response: PichuParkingAPIResponse = api.getParkingLots()
        val responseDateTime: String = response.timestamp
        val responseDate: String = responseDateTime.substring(0, 10)
        assertEquals(currentDate, responseDate)
    }
    @Test
    fun testGetParkingRates() {
        val currentDate: String = LocalDate.now().toStr()
        val response: PichuParkingAPIResponse = api.getParkingRates()
        val responseDateTime: String = response.timestamp
        val responseDate: String = responseDateTime.substring(0, 10)
        assertEquals(currentDate, responseDate)
    }
}