package com.tomaszezula.makker.client.jvm

import com.tomaszezula.makker.client.jvm.model.ModuleUpdate
import com.tomaszezula.makker.common.model.Blueprint
import com.tomaszezula.makker.common.model.Scenario
import com.tomaszezula.makker.common.model.Scheduling
import com.tomaszezula.makker.common.model.UpdateResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.future
import java.nio.file.Path
import java.util.concurrent.CompletableFuture

class DefaultJavaMakeClient(private val makeClient: MakeClient) : JavaMakeClient {

    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)

    override fun createScenario(
        teamId: Long,
        folderId: Long,
        blueprint: Blueprint.Json,
        scheduling: Scheduling
    ): CompletableFuture<Scenario> =
        asCompletableFuture {
            makeClient.createScenario(Scenario.TeamId(teamId), Scenario.FolderId(folderId), blueprint, scheduling)
        }

    override fun createScenario(
        teamId: Long,
        folderId: Long,
        filePath: Path,
        scheduling: Scheduling
    ): CompletableFuture<Scenario> =
        asCompletableFuture {
            makeClient.createScenario(Scenario.TeamId(teamId), Scenario.FolderId(folderId), filePath, scheduling)
        }

    override fun updateScenario(scenarioId: Long, blueprint: Blueprint.Json): CompletableFuture<Scenario> =
        asCompletableFuture {
            makeClient.updateScenario(Scenario.Id(scenarioId), blueprint)
        }

    override fun updateScenario(scenarioId: Long, filePath: Path): CompletableFuture<Scenario> =
        asCompletableFuture {
            makeClient.updateScenario(Scenario.Id(scenarioId), filePath)
        }

    override fun getBlueprint(scenarioId: Long): CompletableFuture<Blueprint> =
        asCompletableFuture {
            makeClient.getBlueprint(Scenario.Id(scenarioId))
        }

    override fun getBlueprints(scenarioIds: List<Long>): CompletableFuture<List<Blueprint>> =
        asCompletableFuture {
            makeClient.getBlueprints(scenarioIds.map { Scenario.Id(it) })
        }

    override fun setModuleData(
        scenarioId: Long,
        moduleId: Long,
        key: String,
        value: String
    ): CompletableFuture<UpdateResult> =
        asCompletableFuture {
            makeClient.setModuleData(Scenario.Id(scenarioId), Blueprint.Module.Id(moduleId), key, value)
        }

    override fun setModuleData(
        scenarioId: Long,
        moduleUpdates: List<ModuleUpdate>
    ): CompletableFuture<UpdateResult> =
        asCompletableFuture {
            makeClient.setModuleData(Scenario.Id(scenarioId), moduleUpdates)
        }

    private fun <T> asCompletableFuture(f: suspend () -> Result<T>): CompletableFuture<T> =
        scope.future {
            f().getOrThrow()
        }
}
