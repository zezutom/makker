package com.tomaszezula.makker.client.jvm.examples

import com.tomaszezula.makker.client.jvm.examples.Config.makeClient
import com.tomaszezula.makker.client.jvm.examples.Config.updatedBlueprint
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
    makeClient.updateScenario(
        Scenario.Id(473703),
        Blueprint.Json(updatedBlueprint)
    ).onSuccess {
        println(it)
    }.onFailure {
        it.printStackTrace()
    }
}

private suspend fun fromEncodedBlueprint() {
    makeClient.updateScenario(
        Scenario.Id(473703),
        Blueprint.Json(
            Base64.getEncoder().encodeToString(updatedBlueprint.toByteArray()),
            encoded = true
        )
    ).onSuccess {
        println(it)
    }.onFailure {
        it.printStackTrace()
    }
}

private suspend fun fromFile() {
    makeClient.updateScenario(
        Scenario.Id(473703),
        Path.of(Config.getResource("blueprint-updated.json").toURI())
    ).onSuccess {
        println(it)
    }.onFailure {
        it.printStackTrace()
    }
}