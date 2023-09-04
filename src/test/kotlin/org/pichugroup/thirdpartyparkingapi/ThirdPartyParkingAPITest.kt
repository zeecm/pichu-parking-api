package org.pichugroup.thirdpartyparkingapi

import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import org.pichugroup.schema.*
import kotlin.test.Test
import kotlin.test.assertEquals

class UtilityTest {

    @Test
    fun testTextJsonToMap() {
        val input = "{key1:value1, key2:value2, key3:{nestedKey3:value3}}"
        val expectedMap: Map<String, Any> = mapOf(
            "key1" to "value1",
            "key2" to "value2",
            "key3" to mapOf("nestedKey3" to "value3"),
        )
        input.checkTextJsonToMap(expectedMap)
    }

    private fun String.checkTextJsonToMap(expectedOutput: Map<String, Any>) {
        val convertedMap: Map<String, Any> = textJsonToMap(this)
        assertEquals(expectedOutput, convertedMap)
    }
}

class URAParkingAPITest {
    private val uraAccessKey: String = System.getenv("URA_ACCESS_KEY") ?: ""

    companion object {
        const val PARKING_LOT_RESPONSE = """{
                                  "Status": "Success",
                                  "Message": "",
                                  "Result": [{
                                      "lotsAvailable": "0",
                                      "lotType": "M",
                                      "carparkNo": "N0006",
                                      "geometries": [{
                                        "coordinates": "28956.4609, 29088.2522"
                                      }]
                                    },
                                    {
                                      "lotsAvailable": "2",
                                      "lotType": "M",
                                      "carparkNo": "S0108",
                                      "geometries": [{
                                        "coordinates": "29930.895, 33440.7746"
                                      }]
                                    }
                                  ]
                                }"""
        const val PARKING_RATES_RESPONSE = """{
                                  "Status": "Success",
                                  "Message": "",
                                  "Result": [{
                                      "weekdayMin": "30mins",
                                      "ppName": "ALIWAL STREET",
                                      "endTime": "05.00 PM",
                                      "weekdayRate": "$0.50",
                                      "startTime": "08.30 AM",
                                      "ppCode": "A0004",
                                      "sunPHRate": "$0.50",
                                      "satdayMin": "30 mins",
                                      "sunPHMin": "30 mins",
                                      "parkingSystem": "C",
                                      "parkCapacity": 69,
                                      "vehCat": "Car",
                                      "satdayRate": "$0.50",
                                      "geometries": [{
                                          "coordinates": "31045.6165, 31694.0055"
                                        },
                                        {
                                          "coordinates": "31126.0755, 31564.9876"
                                        }
                                      ]
                                    }
                                  ]
                                 }"""
        val parkingLotMockEngine = MockEngine { _ ->
            respond(
                content = ByteReadChannel(PARKING_LOT_RESPONSE),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val parkingRatesMockEngine = MockEngine {
            respond(
                content = ByteReadChannel(PARKING_RATES_RESPONSE),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
    }

    @Test
    fun testGetToken() {
        runBlocking {
            val mockEngine = MockEngine { _ ->
                respond(
                    content = ByteReadChannel("""{"Result": "mocked_token"}"""),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
            val api = URAParkingAPIClient(engine = mockEngine, apiKey = uraAccessKey)
            val token = api.getToken()
            assertEquals("mocked_token", token)
        }
    }

    @Test
    fun testGetParkingLots() {
        runBlocking {
            val expectedCarparkIDs = setOf("N0006", "S0108")
            val api = URAParkingAPIClient(engine = parkingLotMockEngine, apiKey = uraAccessKey)
            val parkingLots: Set<PichuParkingLots> = api.getParkingLots()
            val parkingLotIDs: Set<String> = parkingLots.map { it.carparkID }.toSet()
            assertEquals(expectedCarparkIDs, parkingLotIDs)
        }
    }

    @Test
    fun testDeserializeParkingLotResponse() {
        val expectedCarparkIDs = setOf("N0006", "S0108")
        val uraParkingLotResponse: URAParkingLotResponse = deserializeJsonTextToSchema(PARKING_LOT_RESPONSE)
        val parkingLotIDs: Set<String> = uraParkingLotResponse.result.map { it.carparkNo }.toSet()
        assertEquals(expectedCarparkIDs, parkingLotIDs)
    }

    @Test
    fun testGetParkingRates() {
        runBlocking {
            val expectedCarparkName = "ALIWAL STREET"
            val api = URAParkingAPIClient(engine = parkingRatesMockEngine, apiKey = uraAccessKey)
            val parkingRates: Set<PichuParkingRates> = api.getParkingRates()
            assertEquals(expectedCarparkName, parkingRates.elementAt(0).carparkName)
        }
    }

    @Test
    fun testDeserializeParkingRatesResponse() {
        val expectedCarparkName = "ALIWAL STREET"
        val uraParkingRatesResponse: URAParkingRatesResponse = deserializeJsonTextToSchema(PARKING_RATES_RESPONSE)
        assertEquals(uraParkingRatesResponse.result[0].ppName, expectedCarparkName)
    }
}

class LTAParkingAPITest {
    private val ltaAccountKey: String = System.getenv("LTA_ACCOUNT_KEY") ?: ""

    companion object {
        const val PARKING_LOT_RESPONSE = """{
                                "odata.metadata": "http://datamall2.mytransport.sg/ltaodataservice/metadata#CarParkAvailability",
                                "value": [
                                            {
                                                "CarParkID": "1",
                                                "Area": "Marina",
                                                "Development": "Suntec City",
                                                "Location": "1.29375 103.85718",
                                                "AvailableLots": 1104,
                                                "LotType": "C",
                                                "Agency": "LTA"
                                            }
                                    ]
                                }"""
        val parkingLotsMockEngine = MockEngine { _ ->
            respond(
                content = ByteReadChannel(PARKING_LOT_RESPONSE),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
    }

    @Test
    fun testGetParkingLots() {
        runBlocking {
            val api = LTAParkingAPIClient(engine = parkingLotsMockEngine, apiKey = ltaAccountKey)
            val parkingLots: Set<PichuParkingLots> = api.getParkingLots()
            val availableLots = parkingLots.map { it.availableLots }
            val expectedAvailableLots: List<Int> = listOf(1104)
            assertEquals(availableLots, expectedAvailableLots)
        }
    }

    @Test
    fun testDeserializeParkingLotResponse() {
        val expectedAvailableLots: List<Int> = listOf(1104)
        val parkingLotResponse: LTAParkingAvailabilityResponse = deserializeJsonTextToSchema(PARKING_LOT_RESPONSE)
        val actualAvailableLots: List<Int> = parkingLotResponse.value.map { it.availableLots }
        assertEquals(expectedAvailableLots, actualAvailableLots)
    }
}