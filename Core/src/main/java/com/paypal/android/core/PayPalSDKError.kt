package com.paypal.android.core

/**
 * Class used to describe PayPal errors when they occur.
 */
data class PayPalSDKError(
    val code: Int,
    val errorDescription: String,
    val correlationID: String? = null
) : Exception("Error: $code - Description: $errorDescription")
