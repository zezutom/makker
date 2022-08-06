package com.tomaszezula.makker.client.jvm

import com.tomaszezula.makker.adapter.MakeAdapter
import com.tomaszezula.makker.adapter.model.Blueprint
import com.tomaszezula.makker.adapter.model.Scenario
import com.tomaszezula.makker.adapter.model.Scheduling
import kotlinx.coroutines.*
import java.nio.file.Files
import java.nio.file.Path
import java.util.Base64

class MakeClientImpl(private val makeAdapter: MakeAdapter) : MakeClient {

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
        makeAdapter.createScenario(teamId, folderId, blueprintJson.toPlainText(encoded), scheduling)

    override suspend fun createScenario(
        teamId: Scenario.TeamId,
        folderId: Scenario.FolderId,
        scheduling: Scheduling,
        filePath: Path,
        encoded: Boolean
    ): Result<Scenario> =
        makeAdapter.createScenario(teamId, folderId, fromFile(filePath, encoded), scheduling)

    override suspend fun updateScenario(
        scenarioId: Scenario.Id,
        blueprint: Blueprint.Json,
        encoded: Boolean
    ): Result<Scenario> =
        makeAdapter.updateScenario(scenarioId, blueprint.toPlainText(encoded))

    override suspend fun updateScenario(
        scenarioId: Scenario.Id,
        filePath: Path,
        encoded: Boolean
    ): Result<Scenario> =
        makeAdapter.updateScenario(scenarioId, fromFile(filePath, encoded))

    override suspend fun getBlueprint(scenarioId: Scenario.Id): Result<Blueprint> =
        makeAdapter.getBlueprint(scenarioId)

    override suspend fun getBlueprints(scenarioIds: List<Scenario.Id>): Result<List<Blueprint>> =
        scenarioIds.map {
            coroutineScope {
                async {
                    makeAdapter.getBlueprint(it)
                }
            }
        }.awaitAll().toResult()

    override suspend fun setModuleData(
        scenarioId: Scenario.Id,
        moduleId: Blueprint.Module.Id,
        fieldName: String,
        data: String
    ): Result<Boolean> =
        makeAdapter.setModuleData(scenarioId, moduleId, fieldName, data)

    override suspend fun setModuleData(
        scenarioId: Scenario.Id,
        moduleId: Blueprint.Module.Id,
        fieldMap: Map<String, String>
    ): Result<Boolean> =
        fieldMap.entries.map {
            coroutineScope {
                async {
                    makeAdapter.setModuleData(scenarioId, moduleId, it.key, it.value)
                }
            }
        }.awaitAll().toResult().map { rs -> rs.all { it } }

    private suspend fun fromFile(filePath: Path, encoded: Boolean): Blueprint.Json {
        val contents = withContext(Dispatchers.IO) {
            Files.readAllLines(filePath)
        }.joinToString(Separator) { it.trim() }
        return Blueprint.Json(contents).toPlainText(encoded)
    }


    private fun Blueprint.Json.decode(): Blueprint.Json =
        Blueprint.Json(Base64.getDecoder().decode(this.value).toString())

    private fun Blueprint.Json.toPlainText(encoded: Boolean): Blueprint.Json =
        if (encoded) this.decode() else this

    private fun <T> List<Result<T>>.toResult(): Result<List<T>> =
        if (this.any { it.isFailure }) {
            Result.failure(IllegalStateException("Operation failed for one or more fields."))
        } else {
            Result.success(this.map { it.getOrThrow() })
        }
}