package com.tomaszezula.makker.server.handler

import com.tomaszezula.makker.common.MakeAdapter
import com.tomaszezula.makker.common.model.AuthToken
import com.tomaszezula.makker.common.model.Blueprint
import com.tomaszezula.makker.common.model.Scenario
import com.tomaszezula.makker.common.model.UpdateResult
import com.tomaszezula.makker.common.toResult
import com.tomaszezula.makker.server.model.Ok
import com.tomaszezula.makker.server.model.Response
import com.tomaszezula.makker.server.model.SetModuleDataRequest
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class SetModuleDataHandler(private val makeAdapter: MakeAdapter) :
    Handler<SetModuleDataRequest> {
    override suspend fun handle(request: SetModuleDataRequest, token: AuthToken): Result<Response> =
        request.modules.map {
            coroutineScope {
                async {
                    makeAdapter.setModuleData(
                        Scenario.Id(request.scenarioId!!),
                        Blueprint.Module.Id(it.moduleId),
                        it.key,
                        it.value,
                        token
                    )
                }
            }
        }.awaitAll().toResult().map { rs ->
            Ok(UpdateResult(rs.all { it.result }))
        }
}
