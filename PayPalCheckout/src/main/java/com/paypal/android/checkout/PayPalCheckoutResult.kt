package com.paypal.android.checkout

import com.paypal.android.checkout.pojo.Buyer
import com.paypal.android.checkout.pojo.ErrorInfo
import com.paypal.android.checkout.pojo.ShippingAddress

sealed class PayPalCheckoutResult {
    class Success(
        val orderId: String?,
        val payerId: String?,
        val buyer: Buyer? = null,
        val shippingAddress: ShippingAddress? = null
    ) : PayPalCheckoutResult()

    class Failure(val error: ErrorInfo) : PayPalCheckoutResult()
    object Cancellation : PayPalCheckoutResult()
}
