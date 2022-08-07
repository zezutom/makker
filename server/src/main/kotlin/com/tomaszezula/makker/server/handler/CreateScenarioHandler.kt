package com.tomaszezula.makker.server.handler

import com.tomaszezula.makker.adapter.MakeAdapter
import com.tomaszezula.makker.adapter.model.AuthToken
import com.tomaszezula.makker.adapter.model.Blueprint
import com.tomaszezula.makker.adapter.model.IndefiniteScheduling
import com.tomaszezula.makker.adapter.model.Scenario
import com.tomaszezula.makker.server.model.CreateScenarioRequest
import com.tomaszezula.makker.server.model.Created
import com.tomaszezula.makker.server.model.Response

class CreateScenarioHandler(private val makeAdapter: MakeAdapter) :
    Handler<CreateScenarioRequest> {
    override suspend fun handle(request: CreateScenarioRequest, token: AuthToken): Result<Response> =
        makeAdapter.createScenario(
            Scenario.TeamId(request.teamId),
            Scenario.FolderId(request.folderId),
            Blueprint.Json(request.blueprint),
            IndefiniteScheduling(),
            token
        ).map { Created(it) }
}
