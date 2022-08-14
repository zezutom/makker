package com.tomaszezula.makker.client.jvm.java

import com.tomaszezula.makker.client.jvm.java.model.ModuleUpdate
import com.tomaszezula.makker.common.model.*
import java.nio.file.Path
import java.util.concurrent.CompletableFuture

interface MakeClient {

    companion object {
        fun eu(token: String): MakeClient =
            com.tomaszezula.makker.client.jvm.MakeClient.eu(token).asJava()

        fun us(token: String): MakeClient =
            com.tomaszezula.makker.client.jvm.MakeClient.us(token).asJava()
    }

    fun createScenario(
        teamId: Int,
        folderId: Int,
        blueprint: Blueprint.Json,
        scheduling: Scheduling = IndefiniteScheduling(),
    ): CompletableFuture<Scenario>

    fun createScenario(
        teamId: Int,
        folderId: Int,
        filePath: Path,
        scheduling: Scheduling = IndefiniteScheduling(),
    ): CompletableFuture<Scenario>


    fun updateScenario(
        scenarioId: Int,
        blueprint: Blueprint.Json
    ): CompletableFuture<Scenario>

    fun updateScenario(
        scenarioId: Int,
        filePath: Path
    ): CompletableFuture<Scenario>

    fun getBlueprint(scenarioId: Int): CompletableFuture<Blueprint>

    fun getBlueprints(scenarioIds: List<Int>): CompletableFuture<List<Blueprint>>

    fun setModuleData(
        scenarioId: Int,
        moduleId: Int,
        key: String,
        value: String
    ): CompletableFuture<UpdateResult>

    fun setModuleData(
        scenarioId: Int,
        moduleUpdates: List<ModuleUpdate>
    ): CompletableFuture<UpdateResult>
}
