package org.pichugroup

import APIGatewayEventPayload
import PichuHandler
import com.google.gson.Gson
import org.junit.jupiter.api.Test
import org.pichugroup.schema.PichuParkingAPIResponse
import org.pichugroup.schema.PichuParkingLots

class HandlerTest {
    private val handler = PichuHandler()
    private val gson = Gson()

    companion object {
        const val parkingLotsPayloadString = """
                                        {
                                            "body-json": {},
                                            "params": {
                                                "path": {},
                                                "querystring": {},
                                                "header": {}
                                            },
                                            "stage-variables": {},
                                            "context": {
                                                "account-id": "your-account-id",
                                                "api-id": "your-api-id",
                                                "api-key": "your-api-key",
                                                "authorizer-principal-id": "your-principal-id",
                                                "caller": "your-caller",
                                                "cognito-authentication-provider": "cognito-auth-provider",
                                                "cognito-authentication-type": "cognito-auth-type",
                                                "cognito-identity-id": "cognito-identity-id",
                                                "cognito-identity-pool-id": "cognito-identity-pool-id",
                                                "http-method": "GET",
                                                "stage": "your-stage",
                                                "source-ip": "source-ip",
                                                "user": "user",
                                                "user-agent": "user-agent",
                                                "user-arn": "user-arn",
                                                "request-id": "request-id",
                                                "resource-id": "your-resource-id",
                                                "resource-path": "/parking-lots"
                                            }
                                        }
                                        """
        const val parkingRatesPayloadString = """
                                        {
                                            "body-json": {},
                                            "params": {
                                                "path": {},
                                                "querystring": {},
                                                "header": {}
                                            },
                                            "stage-variables": {},
                                            "context": {
                                                "account-id": "your-account-id",
                                                "api-id": "your-api-id",
                                                "api-key": "your-api-key",
                                                "authorizer-principal-id": "your-principal-id",
                                                "caller": "your-caller",
                                                "cognito-authentication-provider": "cognito-auth-provider",
                                                "cognito-authentication-type": "cognito-auth-type",
                                                "cognito-identity-id": "cognito-identity-id",
                                                "cognito-identity-pool-id": "cognito-identity-pool-id",
                                                "http-method": "GET",
                                                "stage": "your-stage",
                                                "source-ip": "source-ip",
                                                "user": "user",
                                                "user-agent": "user-agent",
                                                "user-arn": "user-arn",
                                                "request-id": "request-id",
                                                "resource-id": "your-resource-id",
                                                "resource-path": "/parking-rates"
                                            }
                                        }
                                        """
    }

    @Test
    fun testGetRequestParkingLots() {
        val eventPayload = gson.fromJson(parkingLotsPayloadString, APIGatewayEventPayload::class.java)
        val response: PichuParkingAPIResponse = handler.handleRequest(event = eventPayload)
        assert(response.data.elementAt(0) is PichuParkingLots)
    }

    @Test
    fun testGetRequestParkingRates() {
        val eventPayload = gson.fromJson(parkingRatesPayloadString, APIGatewayEventPayload::class.java)
        val response: PichuParkingAPIResponse = handler.handleRequest(event = eventPayload)
        assert(response.data.elementAt(0) is PichuParkingLots)
    }
}