import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestStreamHandler
import com.google.gson.annotations.SerializedName
import io.github.oshai.kotlinlogging.KotlinLogging
import org.pichugroup.pichuparkingapi.PichuParkingAPI
import org.pichugroup.schema.PichuParkingAPIResponse

val logger = KotlinLogging.logger {}

class PichuHandler {
    private val api = PichuParkingAPI()
    fun handleRequest(event: APIGatewayEventPayload? = null, context: Context? = null): PichuParkingAPIResponse {
        logger.info { "Received Event: \n $event" }
        val response: PichuParkingAPIResponse = when (event?.context?.httpMethod) {
            "GET" -> handleGetRequests(event)
            else -> {
                throw IllegalArgumentException("invalid http method or not configured ${event?.context?.httpMethod}")
            }
        }
        return response
    }

    private fun handleGetRequests(event: APIGatewayEventPayload? = null): PichuParkingAPIResponse {
        val response: PichuParkingAPIResponse = when (event?.context?.resourcePath) {
            "/parking-lots" -> api.getParkingLots()
            "/parking-rates" -> api.getParkingRates()
            else -> {
                throw IllegalArgumentException("invalid resource path ${event?.context?.resourcePath}")
            }
        }
        return response
    }
}

data class APIGatewayEventPayload(
    var body: Map<String, String> = emptyMap(),
    var params: Parameters = Parameters(),
    var stageVariables: Map<String, String> = emptyMap(),
    var context: APIGatewayEventContext = APIGatewayEventContext(),
)

data class Parameters(
    var path: Map<String, String> = emptyMap(),
    var queryString: Map<String, String> = emptyMap(),
    var header: Map<String, String> = emptyMap(),
)

data class APIGatewayEventContext(
    var accountId: String = "",
    var apiId: String = "",
    var apiKey: String = "",
    var authorizerPrincipalId: String = "",
    var caller: String = "",
    var cognitoAuthenticationProvider: String = "",
    var cognitoAuthenticationType: String = "",
    var cognitoIdentityId: String = "",
    var cognitoIdentityPoolId: String = "",
    var httpMethod: String = "",
    var stage: String = "",
    var sourceIp: String = "",
    var user: String = "",
    var userAgent: String = "",
    var userArn: String = "",
    var requestId: String = "",
    var resourceId: String = "",
    var resourcePath: String = "",
)