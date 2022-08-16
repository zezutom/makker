package com.tomaszezula.makker.client.jvm

import com.tomaszezula.makker.client.jvm.model.ModuleUpdate
import com.tomaszezula.makker.common.MakeAdapter
import com.tomaszezula.makker.common.model.*
import com.tomaszezula.makker.common.toResult
import kotlinx.coroutines.*
import java.nio.file.Files
import java.nio.file.Path

class DefaultMakeClient(private val makeAdapter: MakeAdapter, private val authToken: AuthToken) : MakeClient {

    companion object {
        const val Separator = ""
    }

    override suspend fun createScenario(
        teamId: Scenario.TeamId,
        folderId: Scenario.FolderId,
        blueprint: Blueprint.Json,
        scheduling: Scheduling
    ): Result<Scenario> =
        makeAdapter.createScenario(blueprint, scheduling, CreateScenarioContext(authToken, teamId, folderId))

    override suspend fun createScenario(
        teamId: Scenario.TeamId,
        folderId: Scenario.FolderId,
        filePath: Path,
        scheduling: Scheduling
    ): Result<Scenario> =
        makeAdapter.createScenario(fromFile(filePath), scheduling, CreateScenarioContext(authToken, teamId, folderId))

    override suspend fun updateScenario(
        scenarioId: Scenario.Id,
        blueprint: Blueprint.Json
    ): Result<Scenario> =
        makeAdapter.updateScenario(blueprint, UpdateScenarioContext(authToken, scenarioId))

    override suspend fun updateScenario(
        scenarioId: Scenario.Id,
        filePath: Path
    ): Result<Scenario> =
        makeAdapter.updateScenario(fromFile(filePath), UpdateScenarioContext(authToken, scenarioId))

    override suspend fun getBlueprint(scenarioId: Scenario.Id): Result<Blueprint> =
        makeAdapter.getBlueprint(scenarioId, authToken)

    override suspend fun getBlueprints(scenarioIds: List<Scenario.Id>): Result<List<Blueprint>> =
        scenarioIds.map {
            coroutineScope {
                async {
                    makeAdapter.getBlueprint(it, authToken)
                }
            }
        }.awaitAll().toResult()

    override suspend fun setModuleData(
        scenarioId: Scenario.Id,
        moduleId: Blueprint.Module.Id,
        key: String,
        value: String
    ): Result<UpdateResult> =
        makeAdapter.setModuleData(key, value, SetModuleDataContext(authToken, scenarioId, moduleId))

    override suspend fun setModuleData(
        scenarioId: Scenario.Id,
        moduleUpdates: List<ModuleUpdate>
    ): Result<UpdateResult> =
        moduleUpdates.map {
            coroutineScope {
                async {
                    makeAdapter.setModuleData(
                        it.key,
                        it.value,
                        SetModuleDataContext(authToken, scenarioId, it.moduleId)
                    )
                }
            }
        }.awaitAll().toResult().map { rs -> UpdateResult(rs.all { it.result }) }

    private suspend fun fromFile(filePath: Path): Blueprint.Json {
        val contents = withContext(Dispatchers.IO) {
            Files.readAllLines(filePath)
        }.joinToString(Separator) { it.trim() }
        return Blueprint.Json(contents)
    }
}
