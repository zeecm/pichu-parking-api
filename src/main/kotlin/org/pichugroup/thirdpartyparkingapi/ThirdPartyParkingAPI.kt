package org.pichugroup.thirdpartyparkingapi

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.statement.*
import io.ktor.client.request.*

private fun createKtorHttpClient(engine: HttpClientEngine?): HttpClient {
    if (engine != null) {
        return HttpClient(engine = engine) {
            install(HttpTimeout) {
                requestTimeoutMillis = 1000
            }
        }
    }
    return HttpClient {
        install(HttpTimeout) {
            requestTimeoutMillis = 1000
        }
    }
}

abstract class ThirdPartyParkingAPI(private val httpClient: HttpClient? = null, engine: HttpClientEngine?) {
    protected open val client: HttpClient = initializeHttpClient(engine)

    private fun initializeHttpClient(engine: HttpClientEngine?): HttpClient {
        return this.httpClient ?: createKtorHttpClient(engine)
    }


    suspend fun makeAPICall(
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


open class URAParkingAPI(httpClient: HttpClient? = null, engine: HttpClientEngine? = null, val accessKey: String) : ThirdPartyParkingAPI(httpClient=httpClient,engine=engine) {

    suspend fun getToken(): String {
        val httpResponse: HttpResponse = this.makeAPICallToGetToken()
        val responseMap: Map<String, Any> = this.convertHttpResponseToMap(httpResponse)
        val token: Any = responseMap["Result"] ?: throw Exception("Null Token Received")
        return token.toString()
    }

    private suspend fun makeAPICallToGetToken(): HttpResponse {
        val headers = mapOf(
            "AccessKey" to accessKey,
            "user-agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36",
            "x-requested-with" to "XMLHttpRequest",
        )
        val response: HttpResponse = this.makeAPICall(endpoint = TOKEN_ENDPOINT, headers = headers)
        if (response.status.value != 200) {
            throw Exception("Failed to authenticate with URA's API")
        }
        return response
    }

    private suspend fun convertHttpResponseToMap(httpResponse: HttpResponse): Map<String, Any> {
        val responseBody: String = httpResponse.body()
        return textJsonToMap(responseBody)
    }

    companion object {
        const val PARKING_LOTS_ENDPOINT: String =
            "https://www.ura.gov.sg/uraDataService/invokeUraDS?service=Car_Park_Availability"
        const val PARKING_LIST_AND_RATES_ENDPOINT: String =
            "https://www.ura.gov.sg/uraDataService/invokeUraDS?service=Car_Park_Details"
        const val TOKEN_ENDPOINT: String = "https://www.ura.gov.sg/uraDataService/insertNewToken.action"
    }
}