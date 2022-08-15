package com.tomaszezula.makker.common

import com.tomaszezula.makker.common.model.*

interface MakeAdapter {
    suspend fun createScenario(
        blueprint: Blueprint.Json,
        scheduling: Scheduling,
        context: CreateScenarioContext
    ): Result<Scenario>

    suspend fun updateScenario(
        blueprint: Blueprint.Json,
        context: UpdateScenarioContext
    ): Result<Scenario>

    suspend fun getBlueprint(scenarioId: Scenario.Id, token: AuthToken): Result<Blueprint>

    suspend fun setModuleData(
        key: String,
        value: String,
        context: SetModuleDataContext
    ): Result<UpdateResult>
}
