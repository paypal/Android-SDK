package com.paypal.android.core

enum class Environment(val url: String) {
    LIVE("https://api.paypal.com"),
    SANDBOX("https://api.sandbox.paypal.com"),
    STAGING("https://api.msmaster.qa.paypal.com"),
    LOCAL("https://localhost.paypal.com:8443"),
//    TEMP_STAGING("https://te-card-gql.qa.paypal.com")
}
