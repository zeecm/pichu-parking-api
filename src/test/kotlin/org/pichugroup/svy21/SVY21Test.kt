package org.pichugroup.svy21

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SVY21Test {
    @Test
    fun convertSVY21ToLatLonTest() {
        val svy21Northing = 29088.2522
        val svy21Easting = 28956.4609

        val expectedLatitudeDegrees = 1.2793322661454025
        val expectedLongitudeDegrees = 103.84191274170962

        val svy21 = SVY21()
        val latLon: LatLonCoordinate = svy21.convertSVY21ToLatLon(svy21Northing, svy21Easting)
        assertEquals(latLon.latitude, expectedLatitudeDegrees, absoluteTolerance = 0.00001)
        assertEquals(latLon.longitude, expectedLongitudeDegrees, absoluteTolerance = 0.00001)
    }
}