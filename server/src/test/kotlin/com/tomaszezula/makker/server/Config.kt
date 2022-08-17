package com.tomaszezula.makker.server

import com.tomaszezula.makker.common.MakeAdapter
import com.tomaszezula.makker.common.model.AuthToken
import com.tomaszezula.makker.server.handler.Handlers
import com.tomaszezula.makker.server.model.AnonymousContext
import com.tomaszezula.makker.server.model.AuthenticatedContext
import com.tomaszezula.makker.server.model.RequestContext
import com.tomaszezula.makker.server.plugins.configureRouting
import com.tomaszezula.makker.server.plugins.configureSerialization
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.assertEquals

val authToken = AuthToken("test")

private val anonymousContext = AnonymousContext

private val authenticatedContext = AuthenticatedContext(authToken)

fun asAnonymous(f: (AnonymousContext) -> Unit) = f(anonymousContext)

fun asAuthenticated(f: (AuthenticatedContext) -> Unit) = f(authenticatedContext)

fun <T> result(value: T, shouldSucceed: Boolean): Result<T> =
    if (shouldSucceed) Result.success(value) else Result.failure(IllegalStateException("It failed!"))

suspend fun <T> assertFailure(response: HttpResponse, result: Result<T>, message: String? = null) {
    assertEquals(HttpStatusCode.InternalServerError, response.status)
    assertEquals(
        """
            "${message ?: result.exceptionOrNull()!!.message!!}"
        """.trim(), response.bodyAsText()
    )
}

fun ApplicationTestBuilder.withApplication(makeAdapter: MakeAdapter, requestContext: RequestContext): Unit =
    application {
        configureRouting(Handlers(makeAdapter), requestContext)
        configureSerialization()
    }

fun HttpRequestBuilder.setHeaders() {
    headers {
        append("X-Auth-Token", authToken.value)
    }
    contentType(ContentType.Application.Json)
}

