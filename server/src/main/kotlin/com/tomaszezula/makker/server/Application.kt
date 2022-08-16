package com.tomaszezula.makker.server

import com.tomaszezula.makker.common.DefaultMakeAdapter
import com.tomaszezula.makker.common.MakeConfig
import com.tomaszezula.makker.common.model.AuthToken
import com.tomaszezula.makker.server.handler.Handlers
import com.tomaszezula.makker.server.model.AnonymousContext
import com.tomaszezula.makker.server.model.AuthenticatedContext
import com.tomaszezula.makker.server.plugins.configureLogging
import com.tomaszezula.makker.server.plugins.configureRetries
import com.tomaszezula.makker.server.plugins.configureRouting
import com.tomaszezula.makker.server.plugins.configureSerialization
import com.typesafe.config.ConfigFactory
import io.ktor.client.*
import io.ktor.client.engine.cio.*
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
        configureLogging()
        configureRetries(config.maxRetries())
    }
    val makeAdapter = DefaultMakeAdapter(config.toMakeConfig(), client, json)

    val context = config.toAuthToken()?.let {
        AuthenticatedContext(it)
    } ?: AnonymousContext

    embeddedServer(Netty, port = config.port, host = config.host) {
        configureRouting(Handlers(makeAdapter), context)
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
