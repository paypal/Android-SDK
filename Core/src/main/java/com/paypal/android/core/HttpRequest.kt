package com.paypal.android.core

import java.net.URL

data class HttpRequest(
    val url: URL,
    val method: HttpMethod,
    val body: String? = null,
    var headers: MutableMap<String, String> = mutableMapOf(),
) {
    var authOverride: String? = null
}
