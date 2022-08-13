package com.tomaszezula.makker.client.jvm.examples

import io.kotest.common.runBlocking
import com.tomaszezula.makker.client.jvm.examples.Config.makeClient
import com.tomaszezula.makker.client.jvm.model.ModuleUpdate
import com.tomaszezula.makker.common.model.Blueprint
import com.tomaszezula.makker.common.model.Scenario

fun main() {
    runBlocking {
        singleModule()
        multipleModules()
    }
}

private suspend fun singleModule() {
    makeClient.setModuleData(
        Scenario.Id(471310),
        Blueprint.Module.Id(9),
        "value",
        "{{5.greeting}}"
    ).onSuccess {
        println(it)
    }.onFailure {
        it.printStackTrace()
    }
}

private suspend fun multipleModules() {
    makeClient.setModuleData(
        Scenario.Id(471310),
        listOf(
            ModuleUpdate(Blueprint.Module.Id(9), "value", "{{5.greeting}}"),
            ModuleUpdate(Blueprint.Module.Id(12), "value", "{{5.greeting}}"),
            ModuleUpdate(Blueprint.Module.Id(13), "json", "{{5.greeting}}")
        )
    ).onSuccess {
        println(it)
    }.onFailure {
        it.printStackTrace()
    }
}
