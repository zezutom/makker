package com.tomaszezula.makker.client

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.utils.io.*
import kotlinx.serialization.json.JsonObject
import org.junit.jupiter.api.fail

suspend fun <T> withMakeClient(data: T, block: suspend (TestContext<T>) -> Unit) {
    val httpClient = httpClient(data)
    block(TestContext(data, httpClient, makeClient(httpClient)))
}

data class TestContext<T>(val response: T, val httpClient: HttpClient, val makeClient: MakeClient)

private fun <T> httpClient(response: T): HttpClient {
    val mockEngine = MockEngine {
        respond(
            content = ByteReadChannel(mapper.writeValueAsString(response)),
            status = HttpStatusCode.OK,
            headers = headersOf(HttpHeaders.ContentType, "application/json")
        )
    }
    return HttpClient(mockEngine)
}

private fun makeClient(httpClient: HttpClient): MakeClient = DefaultMakeClient(httpClient, mapper, config)

fun HttpClient.mockEngine(): MockEngine = (this.engine as MockEngine)

fun HttpClient.shouldGet(url: String) = verifyRequest(url, HttpMethod.Get)

fun HttpClient.shouldPost(url: String, payload: JsonObject) =
    verifyRequest(url, HttpMethod.Post, payload)

private fun HttpClient.verifyRequest(url: String, method: HttpMethod, payload: JsonObject? = null) {
    val requestHistory = this.mockEngine().requestHistory
    requestHistory shouldHaveSize 1

    val requestData = requestHistory.first()
    requestData.url.toString() shouldBe url
    requestData.method shouldBe method
    requestData.headers.contains(
        HttpHeaders.Authorization,
        "Token ${config.token.value}"
    ) shouldBe true
    payload?.let { json ->
        when (val body = requestData.body) {
            is TextContent -> {
                body.contentType shouldBe ContentType.Application.Json
                body.text shouldBe json.toString()
            }
            else -> fail("Unexpected request payload: $body")
        }
    }
}
