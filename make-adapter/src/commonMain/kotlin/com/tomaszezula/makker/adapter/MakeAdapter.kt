package com.tomaszezula.makker.adapter

import com.tomaszezula.makker.adapter.model.AuthToken
import com.tomaszezula.makker.adapter.model.Blueprint
import com.tomaszezula.makker.adapter.model.Blueprint.Module
import com.tomaszezula.makker.adapter.model.Scenario
import com.tomaszezula.makker.adapter.model.Scenario.TeamId
import com.tomaszezula.makker.adapter.model.Scenario.FolderId
import com.tomaszezula.makker.adapter.model.Scheduling

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
    ): Result<Boolean>
}
