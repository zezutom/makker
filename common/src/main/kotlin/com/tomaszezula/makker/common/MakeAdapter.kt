package com.tomaszezula.makker.common

import com.tomaszezula.makker.common.model.*
import com.tomaszezula.makker.common.model.Blueprint.Module
import com.tomaszezula.makker.common.model.Scenario.FolderId
import com.tomaszezula.makker.common.model.Scenario.TeamId

interface MakeAdapter {
    suspend fun createScenario(
        teamId: TeamId,
        folderId: FolderId,
        blueprintJson: Blueprint.Json,
        scheduling: Scheduling,
        token: AuthToken
    ): Result<Scenario>

    suspend fun updateScenario(
        scenarioId: Scenario.Id,
        blueprint: Blueprint.Json,
        token: AuthToken
    ): Result<Scenario>

    suspend fun getBlueprint(scenarioId: Scenario.Id, token: AuthToken): Result<Blueprint>

    suspend fun setModuleData(
        scenarioId: Scenario.Id,
        moduleId: Module.Id,
        fieldName: String,
        data: String,
        token: AuthToken
    ): Result<UpdateResult>
}
