package com.paypal.android.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection

class Http(private val dispatcher: CoroutineDispatcher = Dispatchers.IO) {

    suspend fun send(request: HttpRequest) =
        withContext(dispatcher) {
            val url = request.url
            val connection = url.openConnection() as HttpURLConnection

            connection.requestMethod = "GET"
            val responseCode = connection.responseCode

            HttpResult(responseCode)
        }
}
