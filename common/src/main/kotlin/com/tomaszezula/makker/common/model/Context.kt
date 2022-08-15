package com.tomaszezula.makker.common.model

sealed interface Context {
    val authToken: AuthToken
}

data class CreateScenarioContext(
    override val authToken: AuthToken,
    val teamId: Scenario.TeamId,
    val folderId: Scenario.FolderId
) : Context

data class UpdateScenarioContext(override val authToken: AuthToken, val scenarioId: Scenario.Id) : Context

data class SetModuleDataContext(
    override val authToken: AuthToken,
    val scenarioId: Scenario.Id,
    val moduleId: Blueprint.Module.Id,
) : Context
