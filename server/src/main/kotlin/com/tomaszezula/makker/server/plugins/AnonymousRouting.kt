package com.tomaszezula.makker.server.plugins

import com.tomaszezula.makker.server.handler.anonymous.CreateScenarioHandler
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureAnonymousRouting(createScenarioHandler: CreateScenarioHandler) {
    routing {
        post("/scenario") {

        }
    }
}