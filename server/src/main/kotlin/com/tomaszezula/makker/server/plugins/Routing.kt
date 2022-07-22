package com.tomaszezula.makker.server.plugins

import com.tomaszezula.makker.client.DefaultMakeClient
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting(client: DefaultMakeClient) {
    routing {
        post("/scenarios") {
//            client.scenario.create(
//                Team.Id(1), Folder.Id(2), Scenario.Blueprint("{}"), IndefiniteScheduling(900)
//            ).onSuccess { scenario ->
//                // TODO 201
//            }.onFailure { exception ->
//                // TODO 500
//            }
        }
    }
}