package org.pichugroup.thirdpartyparkingapi

import kotlin.test.Test
import kotlin.test.assertEquals

class ThirdPartyAPITestClass: ThirdPartyParkingAPI() {}

class ThirdPartyParkingAPITest {

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