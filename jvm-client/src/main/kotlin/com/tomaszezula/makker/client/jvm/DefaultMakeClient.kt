package com.tomaszezula.makker.client.jvm

import com.tomaszezula.makker.common.MakeAdapter
import com.tomaszezula.makker.common.model.*
import kotlinx.coroutines.*
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

class DefaultMakeClient(private val makeAdapter: MakeAdapter, private val token: AuthToken) : MakeClient {

    companion object {
        const val Separator = ""
    }

    override suspend fun createScenario(
        teamId: Scenario.TeamId,
        folderId: Scenario.FolderId,
        blueprintJson: Blueprint.Json,
        scheduling: Scheduling,
        encoded: Boolean
    ): Result<Scenario> =
        makeAdapter.createScenario(teamId, folderId, blueprintJson.toPlainText(encoded), scheduling, token)

    override suspend fun createScenario(
        teamId: Scenario.TeamId,
        folderId: Scenario.FolderId,
        scheduling: Scheduling,
        filePath: Path
    ): Result<Scenario> =
        makeAdapter.createScenario(teamId, folderId, fromFile(filePath), scheduling, token)

    override suspend fun updateScenario(
        scenarioId: Scenario.Id,
        blueprint: Blueprint.Json,
        encoded: Boolean
    ): Result<Scenario> =
        makeAdapter.updateScenario(scenarioId, blueprint.toPlainText(encoded), token)

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
        fieldName: String,
        data: String
    ): Result<UpdateResult> =
        makeAdapter.setModuleData(scenarioId, moduleId, fieldName, data, token)

    override suspend fun setModuleData(
        scenarioId: Scenario.Id,
        moduleId: Blueprint.Module.Id,
        fieldMap: Map<String, String>
    ): Result<UpdateResult> =
        fieldMap.entries.map {
            coroutineScope {
                async {
                    makeAdapter.setModuleData(scenarioId, moduleId, it.key, it.value, token)
                }
            }
        }.awaitAll().toResult().map { rs -> UpdateResult(rs.all { it.result }) }

    private suspend fun fromFile(filePath: Path): Blueprint.Json {
        val contents = withContext(Dispatchers.IO) {
            Files.readAllLines(filePath)
        }.joinToString(Separator) { it.trim() }
        return Blueprint.Json(contents)
    }


    private fun Blueprint.Json.decode(): Blueprint.Json =
        Blueprint.Json(String(Base64.getDecoder().decode(this.value)))

    private fun Blueprint.Json.toPlainText(encoded: Boolean): Blueprint.Json =
        if (encoded) this.decode() else this

    private fun <T> List<Result<T>>.toResult(): Result<List<T>> =
        if (this.any { it.isFailure }) {
            Result.failure(IllegalStateException("Operation failed for one or more fields."))
        } else {
            Result.success(this.map { it.getOrThrow() })
        }
}