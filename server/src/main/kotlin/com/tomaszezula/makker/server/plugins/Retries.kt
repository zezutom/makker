package com.tomaszezula.makker.server.plugins

import io.ktor.client.*
import io.ktor.client.network.sockets.*
import io.ktor.client.plugins.*
import io.ktor.http.*

fun HttpClientConfig<*>.configureRetries(maxRetries: Int) {
    install(HttpRequestRetry) {
        this.maxRetries = maxRetries
        retryIf { _, response ->
            !response.status.isSuccess()
        }
        retryOnExceptionIf { _, cause ->
            cause.isTimeoutException()
        }
        exponentialDelay()
    }
}

private fun Throwable.isTimeoutException(): Boolean = when (this) {
    is HttpRequestTimeoutException -> true
    is ConnectTimeoutException -> true
    is SocketTimeoutException -> true
    else -> false
}