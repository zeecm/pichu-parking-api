package org.pichugroup

import APIGatewayEventPayload
import PichuHandler
import com.google.gson.Gson
import org.junit.jupiter.api.Test
import org.pichugroup.schema.PichuParkingAPIResponse
import org.pichugroup.schema.PichuParkingLots
import org.pichugroup.schema.PichuParkingRates

class HandlerTest {
    private val handler = PichuHandler()
    private val gson = Gson()

    companion object {
        const val parkingLotsPayloadString = """
                                        {
                                            "body": {},
                                            "params": {
                                                "path": {},
                                                "queryString": {},
                                                "header": {}
                                            },
                                            "stage-stageVariables": {},
                                            "context": {
                                                "accountId" : "186157170780",
                                                "apiId" : "q7p4ehtedd",
                                                "apiKey" : "test-invoke-api-key",
                                                "authorizerPrincipalId" : "",
                                                "caller" : "186157170780",
                                                "cognitoAuthenticationProvider" : "",
                                                "cognitoAuthenticationType" : "",
                                                "cognitoIdentityId" : "",
                                                "cognitoIdentityPoolId" : "",
                                                "httpMethod" : "GET",
                                                "stage" : "test-invoke-stage",
                                                "sourceIp" : "test-invoke-source-ip",
                                                "user" : "186157170780",
                                                "userAgent" : "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36 Edg/116.0.1938.69",
                                                "userArn" : "arn:aws:iam::186157170780:root",
                                                "requestId" : "f9a436db-3b3b-4d25-95b8-cadf929a4e4b",
                                                "resourceId" : "zv3nht",
                                                "resourcePath": "/parking-lots"
                                            }
                                        }
                                        """
        const val parkingRatesPayloadString = """
                                        {
                                            "body": {},
                                            "params": {
                                                "path": {},
                                                "queryString": {},
                                                "header": {}
                                            },
                                            "stage-stageVariables": {},
                                            "context": {
                                                "accountId" : "186157170780",
                                                "apiId" : "q7p4ehtedd",
                                                "apiKey" : "test-invoke-api-key",
                                                "authorizerPrincipalId" : "",
                                                "caller" : "186157170780",
                                                "cognitoAuthenticationProvider" : "",
                                                "cognitoAuthenticationType" : "",
                                                "cognitoIdentityId" : "",
                                                "cognitoIdentityPoolId" : "",
                                                "httpMethod" : "GET",
                                                "stage" : "test-invoke-stage",
                                                "sourceIp" : "test-invoke-source-ip",
                                                "user" : "186157170780",
                                                "userAgent" : "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36 Edg/116.0.1938.69",
                                                "userArn" : "arn:aws:iam::186157170780:root",
                                                "requestId" : "f9a436db-3b3b-4d25-95b8-cadf929a4e4b",
                                                "resourceId" : "zv3nht",
                                                "resourcePath": "/parking-rates"
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
        assert(response.data.elementAt(0) is PichuParkingRates)
    }
}