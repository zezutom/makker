package com.tomaszezula.makker.server.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import com.tomaszezula.makker.client.MakkerClient
import com.tomaszezula.makker.client.model.Folder
import com.tomaszezula.makker.client.model.IndefiniteScheduling
import com.tomaszezula.makker.client.model.Scenario
import com.tomaszezula.makker.client.model.Team

fun Application.configureRouting(client: MakkerClient) {
    routing {
        post("/scenarios") {
            client.scenario.create(
                Team.Id(1), Folder.Id(2), Scenario.Blueprint("{}"), IndefiniteScheduling(900)
            ).onSuccess { scenario ->
                // TODO 201
            }.onFailure { exception ->
                // TODO 500
            }
        }
    }
}