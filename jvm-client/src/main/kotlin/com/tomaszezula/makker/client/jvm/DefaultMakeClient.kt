package com.tomaszezula.makker.client.jvm

import com.tomaszezula.makker.client.jvm.model.ModuleUpdate
import com.tomaszezula.makker.common.MakeAdapter
import com.tomaszezula.makker.common.model.*
import com.tomaszezula.makker.common.toResult
import kotlinx.coroutines.*
import java.nio.file.Files
import java.nio.file.Path

class DefaultMakeClient(private val makeAdapter: MakeAdapter, private val token: AuthToken) : MakeClient {

    companion object {
        const val Separator = ""
    }

    override suspend fun createScenario(
        teamId: Scenario.TeamId,
        folderId: Scenario.FolderId,
        blueprint: Blueprint.Json,
        scheduling: Scheduling
    ): Result<Scenario> =
        makeAdapter.createScenario(teamId, folderId, blueprint, scheduling, token)

    override suspend fun createScenario(
        teamId: Scenario.TeamId,
        folderId: Scenario.FolderId,
        filePath: Path,
        scheduling: Scheduling
    ): Result<Scenario> =
        makeAdapter.createScenario(teamId, folderId, fromFile(filePath), scheduling, token)

    override suspend fun updateScenario(
        scenarioId: Scenario.Id,
        blueprint: Blueprint.Json
    ): Result<Scenario> =
        makeAdapter.updateScenario(scenarioId, blueprint, token)

    override suspend fun updateScenario(
        scenarioId: Scenario.Id,
        filePath: Path
    ): Result<Scenario> =
        makeAdapter.updateScenario(scenarioId, fromFile(filePath), token)

    override suspend fun getBlueprint(scenarioId: Scenario.Id): Result<Blueprint> =
        makeAdapter.getBlueprint(scenarioId, token)

    override suspend fun getBlueprints(scenarioIds: List<Scenario.Id>): Result<List<Blueprint>> =
        scenarioIds.map {
            coroutineScope {
                async {
                    makeAdapter.getBlueprint(it, token)
                }
            }
        }.awaitAll().toResult()

    override suspend fun setModuleData(
        scenarioId: Scenario.Id,
        moduleId: Blueprint.Module.Id,
        key: String,
        value: String
    ): Result<UpdateResult> =
        makeAdapter.setModuleData(scenarioId, moduleId, key, value, token)

    override suspend fun setModuleDataInBulk(
        scenarioId: Scenario.Id,
        moduleUpdates: List<ModuleUpdate>
    ): Result<UpdateResult> =
        moduleUpdates.map {
            coroutineScope {
                async {
                    makeAdapter.setModuleData(scenarioId, it.moduleId, it.key, it.value, token)
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
