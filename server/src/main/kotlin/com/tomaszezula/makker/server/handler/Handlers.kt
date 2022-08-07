package com.tomaszezula.makker.server.handler

import com.tomaszezula.makker.adapter.MakeAdapter
import com.tomaszezula.makker.adapter.model.AuthToken
import com.tomaszezula.makker.server.handler.authenticated.CreateScenarioHandler as AuthenticatedCreateScenarioHandler
import com.tomaszezula.makker.server.handler.anonymous.CreateScenarioHandler as AnonymousCreateScenarioHandler
object CreateScenarioHandler {
    fun authenticated(makeAdapter: MakeAdapter, token: AuthToken): AuthenticatedCreateScenarioHandler =
        AuthenticatedCreateScenarioHandler(makeAdapter, token)
    fun anonymous(makeAdapter: MakeAdapter): AnonymousCreateScenarioHandler =
        AnonymousCreateScenarioHandler(makeAdapter)
}