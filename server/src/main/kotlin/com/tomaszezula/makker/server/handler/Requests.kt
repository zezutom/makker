package com.tomaszezula.makker.server.handler

sealed interface Request

data class CreateScenarioRequest(val teamId: Long, val folderId: Long, val schedulingInterval: Int) : Request
