package com.tomaszezula.makker.server.handler.authenticated

import com.tomaszezula.makker.adapter.MakeAdapter
import com.tomaszezula.makker.adapter.model.AuthToken
import com.tomaszezula.makker.server.handler.CreateScenarioRequest
import com.tomaszezula.makker.server.handler.Response

class CreateScenarioHandler(makeAdapter: MakeAdapter, private val token: AuthToken) :
    Handler<CreateScenarioRequest> {
    private val proxy = com.tomaszezula.makker.server.handler.anonymous.CreateScenarioHandler(makeAdapter)
    override fun handle(request: CreateScenarioRequest): Response =
        proxy.handle(request, token)
}