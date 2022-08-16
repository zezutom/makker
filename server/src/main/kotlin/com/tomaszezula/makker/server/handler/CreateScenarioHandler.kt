package com.tomaszezula.makker.server.handler

import com.tomaszezula.makker.common.MakeAdapter
import com.tomaszezula.makker.common.model.*
import com.tomaszezula.makker.server.model.CreateScenarioRequest
import com.tomaszezula.makker.server.model.Created
import com.tomaszezula.makker.server.model.Response

class CreateScenarioHandler(private val makeAdapter: MakeAdapter) :
    Handler<CreateScenarioRequest> {
    override suspend fun handle(request: CreateScenarioRequest, token: AuthToken): Result<Response> =
        makeAdapter.createScenario(
            Blueprint.Json(request.blueprint),
            IndefiniteScheduling(),
            CreateScenarioContext(
                token,
                Scenario.TeamId(request.teamId),
                Scenario.FolderId(request.folderId)
            )
        ).map { Created(it) }
}
