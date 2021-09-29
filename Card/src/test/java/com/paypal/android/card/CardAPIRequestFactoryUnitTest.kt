package com.paypal.android.card

import io.mockk.every
import io.mockk.mockk
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CardAPIRequestFactoryUnitTest {

    private val orderID = "sample-order-id"
    private val card = Card("4111 1111 1111 1111", "01/22", "123")

    private val dateParser = mockk<DateParser>()

    private lateinit var sut: CardAPIRequestFactory

    @Before
    fun beforeEach() {
        sut = CardAPIRequestFactory(dateParser)
    }

    @Test
    fun `it builds a confirm payment source request`() {
        val cardExpiry = ExpirationDate(1, 2022)
        every { dateParser.parseExpirationDate("01/22") } returns cardExpiry

        val apiRequest = sut.createConfirmPaymentSourceRequest(orderID, card)
        assertEquals("v2/checkout/orders/sample-order-id/confirm-payment-source", apiRequest.path)

        val json = JSONObject(apiRequest.body!!)
        val paymentSource = json.getJSONObject("payment_source")
        val card = paymentSource.getJSONObject("card")
        assertEquals("4111111111111111", card.getString("number"))
        assertEquals("2022-01", card.getString("expiry"))
        assertEquals("123", card.getString("security_code"))
    }
}
