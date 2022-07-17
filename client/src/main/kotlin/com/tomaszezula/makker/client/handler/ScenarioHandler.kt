package com.tomaszezula.makker.client.handler

import com.tomaszezula.makker.client.model.*

class ScenarioHandler {
    fun create(
        teamId: Team.Id,
        folderId: Folder.Id,
        blueprint: Scenario.Blueprint,
        scheduling: Scheduling
    ): Result<Scenario> = TODO()

    fun getBlueprint(
        scenarioId: Scenario.Id,
        draft: Boolean
    ): Result<Scenario.Blueprint> = TODO()

    fun setModuleData(
        scenarioId: Scenario.Id,
        moduleId: Scenario.Module.Id,
        fieldName: String,
        data: Any
    ): Result<Scenario.Module> = TODO()
}
