package com.tomaszezula.makker.client.jvm

import com.tomaszezula.makker.common.model.AuthToken
import com.tomaszezula.makker.common.model.Blueprint
import com.tomaszezula.makker.common.model.IndefiniteScheduling
import com.tomaszezula.makker.common.model.Scenario

val token = AuthToken("test")
val scenarioId = Scenario.Id(1)
val teamId = Scenario.TeamId(1)
val folderId = Scenario.FolderId(1)
val module = Blueprint.Module(Blueprint.Module.Id(1), "Test Module")
val blueprint = Blueprint("Test blueprint", scenarioId, listOf(module), Blueprint.Json("{}"))
val scheduling = IndefiniteScheduling()
val scenario = Scenario(
    Scenario.Id(1),
    teamId,
    folderId,
    "New scenario"
)
