package com.tomaszezula.makker.server.handler

import com.tomaszezula.makker.common.MakeAdapter
import com.tomaszezula.makker.common.model.AuthToken
import com.tomaszezula.makker.common.model.Blueprint
import com.tomaszezula.makker.common.model.IndefiniteScheduling
import com.tomaszezula.makker.common.model.Scenario
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
