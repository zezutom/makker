package com.tomaszezula.makker.server.handler

import com.tomaszezula.makker.adapter.MakeAdapter
import com.tomaszezula.makker.adapter.model.AuthToken
import com.tomaszezula.makker.adapter.model.Blueprint
import com.tomaszezula.makker.adapter.model.Scenario
import com.tomaszezula.makker.server.model.Ok
import com.tomaszezula.makker.server.model.Response
import com.tomaszezula.makker.server.model.UpdateScenarioRequest

class UpdateScenarioHandler(private val makeAdapter: MakeAdapter) :
    Handler<UpdateScenarioRequest> {
    override suspend fun handle(request: UpdateScenarioRequest, token: AuthToken): Result<Response> =
        makeAdapter.updateScenario(
            Scenario.Id(request.scenarioId),
            Blueprint.Json(request.blueprint),
            token
        ).map { Ok(it) }
}
