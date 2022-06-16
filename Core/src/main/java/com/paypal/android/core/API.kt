package com.paypal.android.core

class API internal constructor(
    private val configuration: CoreConfig,
    private val http: Http,
    private val httpRequestFactory: HttpRequestFactory
) {

    constructor(configuration: CoreConfig) :
            this(configuration, Http(), HttpRequestFactory())

    suspend fun send(apiRequest: APIRequest, authOverride: String? = null): HttpResponse {
        val httpRequest =
            httpRequestFactory.createHttpRequestFromAPIRequest(apiRequest, configuration)
        httpRequest.authOverride = authOverride
        return http.send(httpRequest)
    }

    suspend fun send(httpRequest: HttpRequest): HttpResponse {
        httpRequestFactory.authorizeHttpRequest(httpRequest, configuration)
        return http.send(httpRequest)
    }
}
