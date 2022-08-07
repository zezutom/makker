package com.tomaszezula.makker.server.model

sealed interface Request
data class CreateScenarioRequest(val teamId: Long, val folderId: Long, val blueprint: String) : Request
data class UpdateScenarioRequest(val scenarioId: Long, val blueprint: String) : Request
data class GetBlueprintRequest(val scenarioId: Long) : Request
data class SetModuleDataRequest(val scenarioId: Long, val moduleId: Long, val fieldName: String, val data: String) :
    Request
