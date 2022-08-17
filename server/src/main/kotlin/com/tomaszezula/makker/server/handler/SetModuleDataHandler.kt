package com.tomaszezula.makker.server.handler

import com.tomaszezula.makker.common.MakeAdapter
import com.tomaszezula.makker.common.model.*
import com.tomaszezula.makker.common.toResult
import com.tomaszezula.makker.server.model.Ok
import com.tomaszezula.makker.server.model.Response
import com.tomaszezula.makker.server.model.SetModuleDataRequest

class SetModuleDataHandler(private val makeAdapter: MakeAdapter) :
    Handler<SetModuleDataRequest> {
    override suspend fun handle(request: SetModuleDataRequest, token: AuthToken): Result<Response> =
        request.modules.map {
            makeAdapter.setModuleData(
                it.key,
                it.value,
                SetModuleDataContext(
                    token,
                    Scenario.Id(request.scenarioId!!),
                    Blueprint.Module.Id(it.moduleId)
                )
            )
        }.toResult().map { results ->
            Ok(UpdateResult(results.all { it.result }))
        }
}
