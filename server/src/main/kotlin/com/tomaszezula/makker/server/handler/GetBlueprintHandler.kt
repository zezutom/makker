package com.tomaszezula.makker.server.handler

import com.tomaszezula.makker.common.MakeAdapter
import com.tomaszezula.makker.common.model.AuthToken
import com.tomaszezula.makker.common.model.Scenario
import com.tomaszezula.makker.server.model.GetBlueprintRequest
import com.tomaszezula.makker.server.model.Ok
import com.tomaszezula.makker.server.model.Response

class GetBlueprintHandler(private val makeAdapter: MakeAdapter) :
    Handler<GetBlueprintRequest> {
    override suspend fun handle(request: GetBlueprintRequest, token: AuthToken): Result<Response> =
        makeAdapter.getBlueprint(
            Scenario.Id(request.scenarioId),
            token
        ).map { Ok(it) }
}
