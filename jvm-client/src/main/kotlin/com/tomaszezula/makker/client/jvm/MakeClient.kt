package com.tomaszezula.makker.client.jvm

import com.tomaszezula.makker.common.model.Blueprint
import com.tomaszezula.makker.common.model.Scenario
import com.tomaszezula.makker.common.model.Scheduling
import java.nio.file.Path

interface MakeClient {

    suspend fun createScenario(
        teamId: Scenario.TeamId,
        folderId: Scenario.FolderId,
        blueprintJson: Blueprint.Json,
        scheduling: Scheduling,
        encoded: Boolean = false
    ): Result<Scenario>

    suspend fun createScenario(
        teamId: Scenario.TeamId,
        folderId: Scenario.FolderId,
        scheduling: Scheduling,
        filePath: Path
    ): Result<Scenario>

    suspend fun updateScenario(
        scenarioId: Scenario.Id,
        blueprint: Blueprint.Json,
        encoded: Boolean = false
    ): Result<Scenario>

    suspend fun updateScenario(
        scenarioId: Scenario.Id,
        filePath: Path
    ): Result<Scenario>

    suspend fun getBlueprint(scenarioId: Scenario.Id): Result<Blueprint>

    suspend fun getBlueprints(scenarioIds: List<Scenario.Id>): Result<List<Blueprint>>

    suspend fun setModuleData(
        scenarioId: Scenario.Id,
        moduleId: Blueprint.Module.Id,
        fieldName: String,
        data: String
    ): Result<Boolean>

    suspend fun setModuleData(
        scenarioId: Scenario.Id,
        moduleId: Blueprint.Module.Id,
        fieldMap: Map<String, String>
    ): Result<Boolean>
}