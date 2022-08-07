package com.tomaszezula.makker.server.handler.authenticated

import com.tomaszezula.makker.server.handler.Request
import com.tomaszezula.makker.server.handler.Response

interface Handler<T: Request> {
    fun handle(request: T): Response
}
