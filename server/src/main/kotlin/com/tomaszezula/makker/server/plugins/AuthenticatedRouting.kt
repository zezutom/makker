package com.tomaszezula.makker.server.plugins

import com.tomaszezula.makker.server.handler.authenticated.CreateScenarioHandler
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*

fun Application.configureAuthenticatedRouting(createScenarioHandler: CreateScenarioHandler) {
    routing {
        post("/scenario") {
            createScenarioHandler.handle(call.receive())

//            makeClient.createScenario()
        }
    }
}