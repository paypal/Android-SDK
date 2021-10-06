package com.paypal.android.card

import com.paypal.android.core.APIRequest
import com.paypal.android.core.HttpMethod
import org.json.JSONObject

internal class GraphQLRequestFactory {

    companion object {
        const val path = "graphql"
    }

    fun createPayWithCreditCardRequest(orderID: String, card: Card): APIRequest {
        val cardNumber = card.number.replace("\\s".toRegex(), "")
        val cardExpiry = "${card.expirationYear}-${card.expirationMonth}"

        val query = """
        mutation PROCESS_PAYMENT(
            ${'$'}token: String!,
            ${'$'}clientID: String!,
            ${'$'}paymentMethod: PaymentMethodInput!,
            ${'$'}buttonSessionID: String,
            ${'$'}branded: Boolean!
            ) {
                processPayment(
                    token: ${'$'}token, 
                    clientID: ${'$'}clientID,
                    paymentMethod: ${'$'}paymentMethod,
                    buttonSessionID: ${'$'}buttonSessionID
                    branded: ${'$'}branded
                )
            }
        """.trimIndent()

        val data = JSONObject().apply {
            put("query", query)
            put(
                "variables",
                JSONObject().apply {
                    put("token", orderID)
                    put(
                        "clientID",
                        "ASUApeBhpz9-IhrBRpHbBfVBklK4XOr1lvZdgu1UlSK0OvoJut6R-zPUP7iufxso55Yvyl6IZYV3yr0g"
                    )
                    put(
                        "paymentMethod", JSONObject()
                            .put(
                                "cardInput", JSONObject()
                                    .put("expirationDate", cardExpiry)
                                    .put("cardNumber", cardNumber)
                            )
                            .put("expirationDate", cardExpiry)
                            .put("type", "card")
                    )
                    put("buttonSessionID", "EXAMPLE_BUTTON_SESSION_ID")
                    put("branded", false)
                }
            )
        }

        return APIRequest(path, HttpMethod.POST, data.toString())
    }

    data class CardInput(
        val cardNumber: String,
//        val securityCode: String,
        val expirationDate: String
    )

}