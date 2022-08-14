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
        teamId: Long,
        folderId: Long,
        blueprint: Blueprint.Json,
        scheduling: Scheduling = IndefiniteScheduling(),
    ): CompletableFuture<Scenario>

    fun createScenario(
        teamId: Long,
        folderId: Long,
        filePath: Path,
        scheduling: Scheduling = IndefiniteScheduling(),
    ): CompletableFuture<Scenario>


    fun updateScenario(
        scenarioId: Long,
        blueprint: Blueprint.Json
    ): CompletableFuture<Scenario>

    fun updateScenario(
        scenarioId: Long,
        filePath: Path
    ): CompletableFuture<Scenario>

    fun getBlueprint(scenarioId: Long): CompletableFuture<Blueprint>

    fun getBlueprints(scenarioIds: List<Long>): CompletableFuture<List<Blueprint>>

    fun setModuleData(
        scenarioId: Long,
        moduleId: Long,
        key: String,
        value: String
    ): CompletableFuture<UpdateResult>

    fun setModuleData(
        scenarioId: Long,
        moduleUpdates: List<ModuleUpdate>
    ): CompletableFuture<UpdateResult>
}
