package com.tomaszezula.makker.adapter

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
        blueprint: Blueprint.Json,
        scheduling: Scheduling
    ): Result<Scenario>

    suspend fun updateScenario(
        scenarioId: Scenario.Id,
        blueprint: Blueprint.Json
    ): Result<Scenario>

    suspend fun getBlueprint(scenarioId: Scenario.Id): Result<Blueprint>

    suspend fun setModuleData(
        scenarioId: Scenario.Id,
        moduleId: Module.Id,
        fieldName: String,
        data: String
    ): Result<Boolean>
}
