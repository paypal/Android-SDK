package com.paypal.android.checkoutweb

/**
 * A result passed to a [PayPalCheckoutListener] when the PayPal flow completes successfully.
 */
data class PayPalCheckoutResult(val orderId: String?, val payerId: String?)
