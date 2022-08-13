package com.tomaszezula.makker.client.jvm

import com.tomaszezula.makker.client.jvm.model.ModuleUpdate
import com.tomaszezula.makker.common.model.*
import java.nio.file.Path

interface MakeClient {

    suspend fun createScenario(
        teamId: Scenario.TeamId,
        folderId: Scenario.FolderId,
        blueprint: Blueprint.Json,
        scheduling: Scheduling = IndefiniteScheduling(),
    ): Result<Scenario>

    suspend fun createScenario(
        teamId: Scenario.TeamId,
        folderId: Scenario.FolderId,
        filePath: Path,
        scheduling: Scheduling = IndefiniteScheduling(),
    ): Result<Scenario>


    suspend fun updateScenario(
        scenarioId: Scenario.Id,
        blueprint: Blueprint.Json
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
        key: String,
        value: String
    ): Result<UpdateResult>

    suspend fun setModuleDataInBulk(
        scenarioId: Scenario.Id,
        moduleUpdates: List<ModuleUpdate>
    ): Result<UpdateResult>
}