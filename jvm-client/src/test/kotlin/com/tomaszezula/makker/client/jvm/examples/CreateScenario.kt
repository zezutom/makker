package com.tomaszezula.makker.client.jvm.examples

import com.tomaszezula.makker.client.jvm.examples.Config.makeClient
import com.tomaszezula.makker.client.jvm.examples.Config.blueprint
import com.tomaszezula.makker.common.model.Blueprint
import com.tomaszezula.makker.common.model.Scenario
import io.kotest.common.runBlocking
import java.nio.file.Path
import java.util.*

fun main() {
    runBlocking {
        fromBlueprint()
        fromEncodedBlueprint()
        fromFile()
    }
}

private suspend fun fromBlueprint() {
    makeClient.createScenario(
        Scenario.TeamId(55228),
        Scenario.FolderId(22143),
        Blueprint.Json(blueprint)
    ).onSuccess {
        println(it)
    }.onFailure {
        it.printStackTrace()
    }
}

private suspend fun fromEncodedBlueprint() {
    makeClient.createScenario(
        Scenario.TeamId(55228),
        Scenario.FolderId(22143),
        Blueprint.Json(
            Base64.getEncoder().encodeToString(blueprint.toByteArray()),
            encoded = true
        )
    ).onSuccess {
        println(it)
    }.onFailure {
        it.printStackTrace()
    }
}

private suspend fun fromFile() {
    makeClient.createScenario(
        Scenario.TeamId(55228),
        Scenario.FolderId(22143),
        Path.of(Config.getResource("blueprint.json").toURI())
    ).onSuccess {
        println(it)
    }.onFailure {
        it.printStackTrace()
    }
}
