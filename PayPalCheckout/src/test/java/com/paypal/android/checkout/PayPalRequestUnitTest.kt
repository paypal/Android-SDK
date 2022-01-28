package com.paypal.android.checkout

import org.junit.Assert.assertEquals
import org.junit.Test

class PayPalRequestUnitTest {

    @Test
    fun `given an order id, PayPalRequest should return the same orderId`() {
        val orderId = "fake_order_id"
        val payPalRequest = PayPalRequest(orderId)
        assertEquals(orderId, payPalRequest.orderID)
    }
}