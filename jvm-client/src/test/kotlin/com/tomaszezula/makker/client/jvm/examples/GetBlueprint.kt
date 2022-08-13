package com.tomaszezula.makker.client.jvm.examples

import io.kotest.common.runBlocking
import com.tomaszezula.makker.client.jvm.examples.Config.makeClient
import com.tomaszezula.makker.common.model.Scenario

fun main() {
    runBlocking {
        single()
        multiple()
    }
}

suspend fun single() {
    makeClient.getBlueprint(Scenario.Id(471310))
        .onSuccess {
            println(it)
        }.onFailure {
            it.printStackTrace()
        }
}

suspend fun multiple() {
    makeClient.getBlueprints(
        listOf(
            Scenario.Id(473703),
            Scenario.Id(471310),
        )
    ).onSuccess {
        println(it)
    }.onFailure {
        it.printStackTrace()
    }
}
