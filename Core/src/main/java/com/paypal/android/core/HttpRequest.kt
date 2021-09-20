package com.paypal.android.core

import java.net.URL

data class HttpRequest(val url: URL, val method: String, val body: String) {
    constructor(url: String) : this(URL(url), "GET", "")
}
