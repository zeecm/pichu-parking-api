package org.pichugroup.pichuparkingapi

import io.mockk.InternalPlatformDsl.toStr
import org.junit.jupiter.api.Test
import org.pichugroup.schema.PichuParkingAPIResponse
import java.time.LocalDate
import kotlin.test.assertEquals

class PichuParkingAPITest {
    @Test
    fun testGetParkingLot() {
        val currentDate: String = LocalDate.now().toStr()
        val response: String = PichuParkingAPI().getParkingLots()
        val responseDate: String = response.substring(14, 24) // start of string is {"Timestamp":"2023-08-17
        assertEquals(currentDate, responseDate)
    }
}