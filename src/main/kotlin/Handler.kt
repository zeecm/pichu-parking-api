import com.amazonaws.services.lambda.runtime.Context
import com.google.gson.annotations.SerializedName
import io.github.oshai.kotlinlogging.KotlinLogging
import org.pichugroup.pichuparkingapi.PichuParkingAPI
import org.pichugroup.schema.PichuParkingAPIResponse

val logger = KotlinLogging.logger {}

class PichuHandler {
    private val api = PichuParkingAPI()
    fun handleRequest(event: APIGatewayEventPayload? = null, context: Context? = null): PichuParkingAPIResponse {
        logger.debug { "Received Event: \n $event" }
        val response: PichuParkingAPIResponse = when (event?.context?.httpMethod) {
            "GET" -> handleGetRequests(event)
            else -> {
                throw IllegalArgumentException("invalid http method or not configured")
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
    @SerializedName("body-json") var body: Map<String, String> = emptyMap(),
    @SerializedName("params") var params: Parameters = Parameters(),
    @SerializedName("stage-variables") var stageVariables: Map<String, String> = emptyMap(),
    @SerializedName("context") var context: APIGatewayEventContext = APIGatewayEventContext(),
)

data class Parameters(
    @SerializedName("path") var path: Map<String, String> = emptyMap(),
    @SerializedName("querystring") var queryString: Map<String, String> = emptyMap(),
    @SerializedName("header") var header: Map<String, String> = emptyMap(),
)

data class APIGatewayEventContext(
    @SerializedName("account-id") var accountId: String = "",
    @SerializedName("api-id") var apiId: String = "",
    @SerializedName("api-key") var apiKey: String = "",
    @SerializedName("authorizer-principal-id") var authorizerPrincipalId: String = "",
    @SerializedName("caller") var caller: String = "",
    @SerializedName("cognito-authentication-provider") var cognitoAuthenticationProvider: String = "",
    @SerializedName("cognito-authentication-type") var cognitoAuthenticationType: String = "",
    @SerializedName("cognito-identity-id") var cognitoIdentityId: String = "",
    @SerializedName("cognito-identity-pool-id") var cognitoIdentityPoolId: String = "",
    @SerializedName("http-method") var httpMethod: String = "",
    @SerializedName("stage") var stage: String = "",
    @SerializedName("source-ip") var sourceIp: String = "",
    @SerializedName("user") var user: String = "",
    @SerializedName("user-agent") var userAgent: String = "",
    @SerializedName("user-arn") var userArn: String = "",
    @SerializedName("request-id") var requestId: String = "",
    @SerializedName("resource-id") var resourceId: String = "",
    @SerializedName("resource-path") var resourcePath: String = "",
)