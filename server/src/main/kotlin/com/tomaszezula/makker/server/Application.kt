package com.tomaszezula.makker.server

import com.tomaszezula.makker.server.plugins.configureRouting
import com.typesafe.config.ConfigFactory
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*

fun main() {
    val config = HoconApplicationConfig(ConfigFactory.load())

    embeddedServer(Netty, port = config.port, host = config.host) {
        install(ContentNegotiation) {
            json()
        }
        configureRouting()
    }.start(wait = true)
}
