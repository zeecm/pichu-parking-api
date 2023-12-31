package org.pichugroup.thirdpartyparkingapi

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import org.pichugroup.schema.*

enum class ThirdPartyParkingAPI(val description: String) {
    URA(description = "ura"), LTA(description = "lta");

    companion object {
        private val descriptionToEnumMap = entries.associateBy { it.description }
        val all: Set<ThirdPartyParkingAPI> = entries.toSet()
        fun fromDescription(description: String): ThirdPartyParkingAPI {
            return descriptionToEnumMap[description] ?: throw IllegalArgumentException("key error: invalid description")
        }
    }
}

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

abstract class ThirdPartyParkingAPIClient(private val httpClient: HttpClient? = null, engine: HttpClientEngine?) {
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

    abstract suspend fun getParkingLots(): Set<PichuParkingLots>

    abstract suspend fun getParkingRates(): Set<PichuParkingRates>
}


class URAParkingAPIClient(httpClient: HttpClient? = null, engine: HttpClientEngine? = null, val apiKey: String) :
    ThirdPartyParkingAPIClient(httpClient = httpClient, engine = engine) {
    private val defaultHeaders = mapOf(
        "AccessKey" to apiKey,
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
        val httpResponse: HttpResponse = makeAPICallToGetToken()
        val responseMap: Map<String, Any> = convertHttpResponseToMap(httpResponse)
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
        val token: String = getToken()
        val augmentedHeader: MutableMap<String, String> = this.defaultHeaders.toMutableMap()
        augmentedHeader["Token"] = token
        return augmentedHeader.toMap()
    }

    override suspend fun getParkingLots(): Set<PichuParkingLots> {
        val augmentedHeader = augmentHeaderWithToken()
        val parkingLotResponse: HttpResponse = makeAPICall(PARKING_LOTS_ENDPOINT, headers = augmentedHeader)
        val uraResponse: URAParkingLotResponse = deserializeJsonTextToSchema(parkingLotResponse.body())
        return translateURAParkingLotResponse(uraResponse)
    }


    override suspend fun getParkingRates(): Set<PichuParkingRates> {
        val augmentedHeader: Map<String, String> = augmentHeaderWithToken()
        val parkingRatesResponse: HttpResponse = makeAPICall(PARKING_LIST_AND_RATES_ENDPOINT, headers = augmentedHeader)
        val uraResponse: URAParkingRatesResponse = deserializeJsonTextToSchema(parkingRatesResponse.body())
        return translateURAParkingRatesResponse((uraResponse))
    }
}

internal class LTAParkingAPIClient(httpClient: HttpClient? = null, engine: HttpClientEngine? = null, val apiKey: String) :
    ThirdPartyParkingAPIClient(httpClient = httpClient, engine = engine) {
    private val defaultHeader = mapOf(
        "AccountKey" to apiKey,
        "accept" to "application/json",
    )

    companion object {
        const val PARKING_AVAILABILITY_ENDPOINT: String =
            "http://datamall2.mytransport.sg/ltaodataservice/CarParkAvailabilityv2"
    }

    override suspend fun getParkingLots(): Set<PichuParkingLots> {
        val parkingLotResponse: HttpResponse = makeAPICall(PARKING_AVAILABILITY_ENDPOINT, headers = defaultHeader)
        val ltaResponse: LTAParkingAvailabilityResponse = deserializeJsonTextToSchema(parkingLotResponse.body())
        return translateLTAParkingAvailabilityResponse(ltaResponse)
    }

    override suspend fun getParkingRates(): Set<PichuParkingRates> {
        return emptySet()
    }
}

internal interface ThirdPartyParkingAPIClientFactory {
    fun createInstance(
        httpClient: HttpClient? = null,
        engine: HttpClientEngine? = null,
        apiKey: String,
    ): ThirdPartyParkingAPIClient
}

internal object LTAParkingAPIClientFactory : ThirdPartyParkingAPIClientFactory {
    override fun createInstance(
        httpClient: HttpClient?,
        engine: HttpClientEngine?,
        apiKey: String,
    ): ThirdPartyParkingAPIClient {
        return LTAParkingAPIClient(httpClient, engine, apiKey)
    }
}

internal object URAParkingAPIClientFactory : ThirdPartyParkingAPIClientFactory {
    override fun createInstance(
        httpClient: HttpClient?,
        engine: HttpClientEngine?,
        apiKey: String,
    ): ThirdPartyParkingAPIClient {
        return URAParkingAPIClient(httpClient, engine, apiKey)
    }
}
