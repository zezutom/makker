package com.tomaszezula.makker.server.model

import kotlinx.serialization.Serializable

sealed interface Request

@Serializable
data class CreateScenarioRequest(val teamId: Int, val folderId: Int, val blueprint: String) : Request

@Serializable
data class UpdateScenarioRequest(val scenarioId: Int? = null, val blueprint: String) : Request

@Serializable
data class GetBlueprintRequest(val scenarioId: Int) : Request

@Serializable
data class SetModuleDataRequest(val scenarioId: Int? = null, val modules: List<Module>) :
    Request {
    @Serializable
    data class Module(val moduleId: Int, val key: String, val value: String)
}
