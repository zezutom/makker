package com.tomaszezula.makker.server.handler

import com.tomaszezula.makker.common.model.AuthToken
import com.tomaszezula.makker.common.runSuspendCatching
import com.tomaszezula.makker.server.model.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import org.slf4j.Logger

const val AuthTokenHeader = "X-Auth-Token"

suspend fun <T : Request> respond(
    handler: Handler<T>,
    context: RequestContext,
    logger: Logger,
    f: suspend (ApplicationCall) -> T
): suspend (ApplicationCall) -> Unit = { call ->
    runSuspendCatching {
        when (context) {
            is AuthenticatedContext ->
                handler.handle(f(call), context.token)
            is AnonymousContext ->
                call.toAuthToken()?.let { token ->
                    handler.handle(f(call), token)
                } ?: throw MissingAuthTokenException()
        }
    }.onSuccess { result ->
        result
            .onSuccess { call.toHttp(it) }
            .onFailure {
                logger.warn("Request failed", it)
                call.toError(it)
            }
    }.onFailure {
        call.toError(it)
    }
}

suspend fun ApplicationCall.toHttp(response: Response) = when (response) {
    is Ok -> {
        this.respond(HttpStatusCode.OK, response.data)
    }
    is Created -> {
        this.respond(HttpStatusCode.Created, response.data)
    }
}

suspend fun ApplicationCall.toError(throwable: Throwable) = when (throwable) {
    is MissingAuthTokenException -> this.respond(HttpStatusCode.BadRequest, "Missing authentication token")
    else -> this.respond(HttpStatusCode.InternalServerError, throwable.message ?: "")
}

fun ApplicationCall.toAuthToken(): AuthToken? =
    this.request.headers[AuthTokenHeader]?.let { AuthToken(it) }
