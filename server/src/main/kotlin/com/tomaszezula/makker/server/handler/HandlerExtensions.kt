package com.tomaszezula.makker.server.handler

import com.tomaszezula.makker.adapter.model.AuthToken
import com.tomaszezula.makker.server.model.*
import com.tomaszezula.makker.server.plugins.runSuspendCatching
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.slf4j.Logger

const val AuthTokenHeader = "X-Auth-Token"

suspend inline fun <reified T : Request> respond(
    handler: Handler<T>,
    context: RequestContext,
    logger: Logger,
    crossinline f: suspend (ApplicationCall) -> T
): suspend (ApplicationCall) -> Unit = { call ->
    runSuspendCatching {
        when (context) {
            is AuthenticatedContext -> handler.handle(call.receive(), context.token)
            is AnonymousContext ->
                call.toAuthToken()?.let { token ->
                    handler.handle(f(call), token).getOrThrow()
                } ?: throw MissingAuthTokenException()
        }
    }.onSuccess {
        call.toHttp(it as Response)
    }.onFailure {
        logger.warn("Request failed", it)
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

