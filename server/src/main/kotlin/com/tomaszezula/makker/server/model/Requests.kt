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
data class SetModuleDataRequest(val scenarioId: Long? = null, val moduleId: Long, val fieldName: String, val data: String) :
    Request
