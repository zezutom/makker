package com.tomaszezula.makker.server.handler.anonymous

import com.tomaszezula.makker.adapter.model.AuthToken
import com.tomaszezula.makker.server.handler.Request
import com.tomaszezula.makker.server.handler.Response

interface Handler<T: Request> {
    fun handle(request: T, token: AuthToken): Response
}
