package com.tomaszezula.makker.client.jvm.examples

import com.tomaszezula.makker.client.jvm.examples.Config.logger
import com.tomaszezula.makker.client.jvm.examples.Config.makeClient
import com.tomaszezula.makker.common.logOnFailure
import com.tomaszezula.makker.common.logOnSuccess
import com.tomaszezula.makker.common.model.Blueprint
import com.tomaszezula.makker.common.model.Scenario
import com.tomaszezula.makker.common.then
import io.kotest.common.runBlocking

fun main() {
    runBlocking {
        makeClient.createScenario(
            Scenario.TeamId(55228),
            Scenario.FolderId(22143),
            Blueprint.Json(Config.blueprint)
        ).logOnSuccess {
            logger.info("Successfully created a new scenario: $it")
        }.logOnFailure {
            logger.warn("Failed to create a new scenario", it)
        }.then { scenario ->
            makeClient.getBlueprint(scenario.id)
                .logOnSuccess {
                    logger.info("Received the scenario's blueprint: $it")
                }.logOnFailure {
                    logger.warn("Failed to receive the scenario's blueprint", it)
                }
        }.onSuccess { blueprint ->
            println("Do stuff with $blueprint")
        }.onFailure { throwable ->
            println("You got an error. Handle it: ${throwable.message}")
        }
    }
}