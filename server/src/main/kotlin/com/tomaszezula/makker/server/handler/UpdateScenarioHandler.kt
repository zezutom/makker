package com.tomaszezula.makker.server.handler

import com.tomaszezula.makker.common.MakeAdapter
import com.tomaszezula.makker.common.model.AuthToken
import com.tomaszezula.makker.common.model.Blueprint
import com.tomaszezula.makker.common.model.Scenario
import com.tomaszezula.makker.common.model.UpdateScenarioContext
import com.tomaszezula.makker.server.model.Ok
import com.tomaszezula.makker.server.model.Response
import com.tomaszezula.makker.server.model.UpdateScenarioRequest

class UpdateScenarioHandler(private val makeAdapter: MakeAdapter) :
    Handler<UpdateScenarioRequest> {
    override suspend fun handle(request: UpdateScenarioRequest, token: AuthToken): Result<Response> =
        makeAdapter.updateScenario(
            Blueprint.Json(request.blueprint),
            UpdateScenarioContext(token, Scenario.Id(request.scenarioId!!))
        ).map { Ok(it) }
}
