package com.tomaszezula.makker.server.model

import kotlinx.serialization.Serializable

sealed interface Request

@Serializable
data class CreateScenarioRequest(val teamId: Long, val folderId: Long, val blueprint: String) : Request

@Serializable
data class UpdateScenarioRequest(val scenarioId: Long? = null, val blueprint: String) : Request

@Serializable
data class GetBlueprintRequest(val scenarioId: Long) : Request

@Serializable
data class SetModuleDataRequest(val scenarioId: Long? = null, val modules: List<Module>) :
    Request {
    @Serializable
    data class Module(val moduleId: Long, val key: String, val value: String)
}
