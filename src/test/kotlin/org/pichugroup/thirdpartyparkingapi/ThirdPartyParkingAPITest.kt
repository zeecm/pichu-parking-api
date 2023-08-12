package org.pichugroup.thirdpartyparkingapi

import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.runBlocking

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

    @Test
    fun testGetToken(){
        runBlocking {
            val mockEngine = MockEngine { _ ->
                respond(
                    content = ByteReadChannel("""{"Result": "mocked_token"}"""),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
            val api = URAParkingAPI(engine = mockEngine, accessKey = uraAccessKey)
            val token = api.getToken()
            assertEquals("mocked_token", token)
        }
    }
}