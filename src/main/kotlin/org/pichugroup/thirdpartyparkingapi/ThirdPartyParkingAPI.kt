package org.pichugroup.thirdpartyparkingapi

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.statement.*
import io.ktor.client.request.*

abstract class ThirdPartyParkingAPI {
    private val client = HttpClient() {
        install(HttpTimeout) {
            requestTimeoutMillis = 1000
        }
    }

    protected suspend fun makeAPICall(
        endpoint: String,
        headers: Map<String, String>? = mapOf(),
        params: Map<String, String>? = mapOf(),
    ): HttpResponse {
        val response: HttpResponse = client.get(endpoint) {
            headers {
                headers?.forEach { (headerName, headerValue) ->
                    append(headerName, headerValue)
                }
            }
            url {
                params?.forEach { (paramName, paramValue) ->
                    parameters.append(paramName, paramValue)
                }
            }
        }
        return response
    }
}


class URAParkingAPI(private val accessKey: String) : ThirdPartyParkingAPI() {
    private val tokenEndpoint: String = "https://www.ura.gov.sg/uraDataService/insertNewToken.action"
    private val parkingLotsEndpoint: String =
        "https://www.ura.gov.sg/uraDataService/invokeUraDS?service=Car_Park_Availability"
    private val parkingListRatesEndpoint: String =
        "https://www.ura.gov.sg/uraDataService/invokeUraDS?service=Car_Park_Details"

    private suspend fun getToken(): String {
        val headers = mapOf<String, String>(
            "AccessKey" to accessKey,
            "user-agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36",
            "x-requested-with" to "XMLHttpRequest",
        )
        val response: HttpResponse = this.makeAPICall(endpoint = this.tokenEndpoint, headers = headers)
        if (response.status.value != 200) {
            throw Exception("Failed to authenticate with URA's API")
        }
        val responseBody: String = response.body()
        val responseMap: Map<String, Any> = textJsonToMap(responseBody)
        val token: Any = responseMap["Result"] ?: throw Exception("Null Token Received")
        return token.toString()
    }
}