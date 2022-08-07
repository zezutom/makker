package com.tomaszezula.makker.server.handler

import com.tomaszezula.makker.adapter.model.AuthToken
import com.tomaszezula.makker.server.model.Request
import com.tomaszezula.makker.server.model.Response

interface Handler<T: Request> {
    suspend fun handle(request: T, token: AuthToken): Result<Response>
}
