package com.tomaszezula.makker.client.jvm

import com.tomaszezula.makker.adapter.model.Blueprint
import com.tomaszezula.makker.adapter.model.IndefiniteScheduling
import com.tomaszezula.makker.adapter.model.Scenario

val teamId = Scenario.TeamId(1)
val folderId = Scenario.FolderId(1)
val module = Blueprint.Module(Blueprint.Module.Id(1), "Test Module")
val blueprint = Blueprint("Test blueprint", listOf(module), Blueprint.Json("{}"))
val scheduling = IndefiniteScheduling()
val scenario = Scenario(
    Scenario.Id(1),
    teamId,
    folderId,
    "New scenario"
)
