package org.pichugroup.thirdpartyparkingapi

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import org.pichugroup.schema.*

private fun createKtorHttpClient(engine: HttpClientEngine?): HttpClient {
    if (engine != null) {
        return HttpClient(engine = engine) {
            install(HttpTimeout) {
                requestTimeoutMillis = 30000
            }
        }
    }
    return HttpClient {
        install(HttpTimeout) {
            requestTimeoutMillis = 30000
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
        if (response.status.value != 200) {
            throw Exception("Failed to get OK response from API")
        }
        return response
    }

    abstract suspend fun getParkingLots(): Set<PichuParkingData>
}


open class URAParkingAPI(httpClient: HttpClient? = null, engine: HttpClientEngine? = null, val accessKey: String) :
    ThirdPartyParkingAPI(httpClient = httpClient, engine = engine) {
    private val defaultHeaders = mapOf(
        "AccessKey" to accessKey,
        "user-agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36",
        "x-requested-with" to "XMLHttpRequest",
    )

    companion object {
        const val PARKING_LOTS_ENDPOINT: String =
            "https://www.ura.gov.sg/uraDataService/invokeUraDS?service=Car_Park_Availability"
        const val PARKING_LIST_AND_RATES_ENDPOINT: String =
            "https://www.ura.gov.sg/uraDataService/invokeUraDS?service=Car_Park_Details"
        const val TOKEN_ENDPOINT: String = "https://www.ura.gov.sg/uraDataService/insertNewToken.action"
    }

    suspend fun getToken(): String {
        val httpResponse: HttpResponse = this.makeAPICallToGetToken()
        val responseMap: Map<String, Any> = this.convertHttpResponseToMap(httpResponse)
        val token: Any = responseMap["Result"] ?: throw Exception("Null Token Received")
        return token.toString()
    }

    private suspend fun convertHttpResponseToMap(httpResponse: HttpResponse): Map<String, Any> {
        val responseBody: String = httpResponse.body()
        return textJsonToMap(responseBody)
    }

    private suspend fun makeAPICallToGetToken(): HttpResponse {
        return makeAPICall(endpoint = TOKEN_ENDPOINT, headers = defaultHeaders)
    }

    private suspend fun augmentHeaderWithToken(): Map<String, String> {
        val token: String = this.getToken()
        val augmentedHeader: MutableMap<String, String> = this.defaultHeaders.toMutableMap()
        augmentedHeader["Token"] = token
        return augmentedHeader.toMap()
    }

    override suspend fun getParkingLots(): Set<PichuParkingData> {
        val augmentedHeader = this.augmentHeaderWithToken()
        val parkingLotResponse: HttpResponse = this.makeAPICall(PARKING_LOTS_ENDPOINT, headers = augmentedHeader)
        val uraResponse: URAParkingLotResponse = deserializeJsonTextToSchema(parkingLotResponse.body())
        return translateURAParkingLotResponse(uraResponse)
    }


    suspend fun getParkingRates(): URAParkingRatesResponse {
        val augmentedHeader: Map<String, String> = this.augmentHeaderWithToken()
        val parkingRatesResponse: HttpResponse =
            this.makeAPICall(PARKING_LIST_AND_RATES_ENDPOINT, headers = augmentedHeader)
        return deserializeJsonTextToSchema(parkingRatesResponse.body())
    }
}

class LTAParkingAPI(httpClient: HttpClient? = null, engine: HttpClientEngine? = null, val accountKey: String) :
    ThirdPartyParkingAPI(httpClient = httpClient, engine = engine) {
    private val defaultHeader = mapOf(
        "AccountKey" to accountKey,
        "accept" to "application/json",
    )

    companion object {
        const val PARKING_AVAILABILITY_ENDPOINT: String =
            "http://datamall2.mytransport.sg/ltaodataservice/CarParkAvailabilityv2"
    }

    override suspend fun getParkingLots(): Set<PichuParkingData> {
        val parkingLotResponse: HttpResponse =
            this.makeAPICall(PARKING_AVAILABILITY_ENDPOINT, headers = this.defaultHeader)
        val ltaResponse: LTAParkingAvailabilityResponse = deserializeJsonTextToSchema(parkingLotResponse.body())
        return translateLTAParkingAvailabilityResponse(ltaResponse)
    }
}