package com.tomaszezula.makker.client.jvm.java

import com.tomaszezula.makker.client.jvm.MakeClient
import com.tomaszezula.makker.client.jvm.java.model.ModuleUpdate
import com.tomaszezula.makker.common.asCompletableFuture
import com.tomaszezula.makker.common.model.Blueprint
import com.tomaszezula.makker.common.model.Scenario
import com.tomaszezula.makker.common.model.Scheduling
import com.tomaszezula.makker.common.model.UpdateResult
import java.nio.file.Path
import java.util.concurrent.CompletableFuture

class DefaultMakeClient(private val makeClient: MakeClient) :
    com.tomaszezula.makker.client.jvm.java.MakeClient {

    override fun createScenario(
        teamId: Int,
        folderId: Int,
        blueprint: Blueprint.Json,
        scheduling: Scheduling
    ): CompletableFuture<Scenario> =
        asCompletableFuture {
            makeClient.createScenario(Scenario.TeamId(teamId), Scenario.FolderId(folderId), blueprint, scheduling)
        }

    override fun createScenario(
        teamId: Int,
        folderId: Int,
        filePath: Path,
        scheduling: Scheduling
    ): CompletableFuture<Scenario> =
        asCompletableFuture {
            makeClient.createScenario(Scenario.TeamId(teamId), Scenario.FolderId(folderId), filePath, scheduling)
        }

    override fun updateScenario(scenarioId: Int, blueprint: Blueprint.Json): CompletableFuture<Scenario> =
        asCompletableFuture {
            makeClient.updateScenario(Scenario.Id(scenarioId), blueprint)
        }

    override fun updateScenario(scenarioId: Int, filePath: Path): CompletableFuture<Scenario> =
        asCompletableFuture {
            makeClient.updateScenario(Scenario.Id(scenarioId), filePath)
        }

    override fun getBlueprint(scenarioId: Int): CompletableFuture<Blueprint> =
        asCompletableFuture {
            makeClient.getBlueprint(Scenario.Id(scenarioId))
        }

    override fun getBlueprints(scenarioIds: List<Int>): CompletableFuture<List<Blueprint>> =
        asCompletableFuture {
            makeClient.getBlueprints(scenarioIds.map { Scenario.Id(it) })
        }

    override fun setModuleData(
        scenarioId: Int,
        moduleId: Int,
        key: String,
        value: String
    ): CompletableFuture<UpdateResult> =
        asCompletableFuture {
            makeClient.setModuleData(Scenario.Id(scenarioId), Blueprint.Module.Id(moduleId), key, value)
        }

    override fun setModuleData(
        scenarioId: Int,
        moduleUpdates: List<ModuleUpdate>
    ): CompletableFuture<UpdateResult> =
        asCompletableFuture {
            makeClient.setModuleData(Scenario.Id(scenarioId), moduleUpdates.map {
                com.tomaszezula.makker.client.jvm.model.ModuleUpdate(
                    Blueprint.Module.Id(it.moduleId), it.key, it.value
                )
            })
        }
}
