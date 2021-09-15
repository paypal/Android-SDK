package com.paypal.android.core

data class PaymentsConfiguration(
    val clientId: String,
    val environment: Environment = Environment.SANDBOX
)
