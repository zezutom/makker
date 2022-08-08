package com.tomaszezula.makker.server.handler

import com.tomaszezula.makker.common.MakeAdapter
import com.tomaszezula.makker.common.model.AuthToken
import com.tomaszezula.makker.common.model.Blueprint
import com.tomaszezula.makker.common.model.Scenario
import com.tomaszezula.makker.server.model.Ok
import com.tomaszezula.makker.server.model.Response
import com.tomaszezula.makker.server.model.SetModuleDataRequest

class SetModuleDataHandler(private val makeAdapter: MakeAdapter) :
    Handler<SetModuleDataRequest> {
    override suspend fun handle(request: SetModuleDataRequest, token: AuthToken): Result<Response> =
        makeAdapter.setModuleData(
            Scenario.Id(request.scenarioId),
            Blueprint.Module.Id(request.moduleId),
            request.fieldName,
            request.data,
            token
        ).map { Ok(it) }
}
