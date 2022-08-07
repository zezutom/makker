package com.tomaszezula.makker.server

import com.tomaszezula.makker.adapter.DefaultMakeAdapter
import com.tomaszezula.makker.adapter.MakeConfig
import com.tomaszezula.makker.adapter.model.AuthToken
import com.tomaszezula.makker.server.handler.CreateScenarioHandler
import com.tomaszezula.makker.server.plugins.configureAnonymousRouting
import com.tomaszezula.makker.server.plugins.configureAuthenticatedRouting
import com.tomaszezula.makker.server.plugins.configureSerialization
import com.typesafe.config.ConfigFactory
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.network.sockets.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.serialization.json.Json

const val AuthTokenProperty = "MAKE_AUTH_TOKEN"
const val RegionProperty = "make.region"

fun main() {
    val json = Json {
        ignoreUnknownKeys = true
    }
    val config = HoconApplicationConfig(ConfigFactory.load())
    val client = HttpClient(CIO) {
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.HEADERS
        }
        install(HttpRequestRetry) {
            maxRetries = config.maxRetries()
            retryIf { _, response ->
                !response.status.isSuccess()
            }
            retryOnExceptionIf { _, cause ->
                cause.isTimeoutException()
            }
            exponentialDelay()
        }
    }
    val makeAdapter = DefaultMakeAdapter(config.toMakeConfig(), client, json)

    embeddedServer(Netty, port = config.port, host = config.host) {
        config.toAuthToken()?.let { token ->
            configureAuthenticatedRouting(
                CreateScenarioHandler.authenticated(makeAdapter, token)
            )
        } ?: run {
            configureAnonymousRouting(
                CreateScenarioHandler.anonymous(makeAdapter)
            )
        }
        configureSerialization()
    }.start(wait = true)
}

private fun HoconApplicationConfig.maxRetries(): Int =
    this.propertyOrNull("ktor.client.retry.max")?.getString()?.toInt() ?: 3

private fun HoconApplicationConfig.toMakeConfig(): MakeConfig =
    this.propertyOrNull(RegionProperty)?.let {
        val region = it.getString().lowercase()
        if (region == "us") MakeConfig.us()
        else MakeConfig.eu()
    } ?: MakeConfig.eu()

private fun HoconApplicationConfig.toAuthToken(): AuthToken? =
    this.propertyOrNull(AuthTokenProperty)?.let { AuthToken(it.toString()) }

private fun Throwable.isTimeoutException(): Boolean = when (this) {
    is HttpRequestTimeoutException -> true
    is ConnectTimeoutException -> true
    is SocketTimeoutException -> true
    else -> false
}