package com.tomaszezula.makker.server.handler.anonymous

import com.tomaszezula.makker.adapter.MakeAdapter
import com.tomaszezula.makker.adapter.model.AuthToken
import com.tomaszezula.makker.server.handler.CreateScenarioRequest
import com.tomaszezula.makker.server.handler.Response

class CreateScenarioHandler(private val makeAdapter: MakeAdapter) :
    Handler<CreateScenarioRequest> {

    override fun handle(request: CreateScenarioRequest, token: AuthToken): Response {

        TODO("Not yet implemented")
    }
}