package com.tomaszezula.makker.common

import com.tomaszezula.makker.common.model.*
import com.tomaszezula.makker.common.model.Blueprint
import com.tomaszezula.makker.common.model.Scenario
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

class MakeAdapterTest {
    companion object {
        const val BlueprintKey = "blueprint"
        const val FolderIdKey = "folderId"
        const val SchedulingKey = "scheduling"
        const val Separator = ""
        const val TeamIdKey = "teamId"
        val token = AuthToken("test-token")
        val scenarioId = Scenario.Id(1)
        val teamId = Scenario.TeamId(1)
        val folderId = Scenario.FolderId(1)
        val blueprint = Blueprint.Json("{}")
        val scheduling = IndefiniteScheduling()
    }

    private val config = MakeConfig(Url("https://test-server.com"))

    @Test
    fun createScenario() {
        runBlocking {
            val engine = MockEngine { requestData ->
                assertRequest(
                    requestData,
                    "${config.baseUrl}/scenarios?confirmed=true",
                    HttpMethod.Post,
                    buildJsonObject {
                        put(BlueprintKey, blueprint.value.lineSequence().map { it.trim() }.joinToString(Separator))
                        put(SchedulingKey, scheduling.toJson())
                        put(TeamIdKey, teamId.value)
                        put(FolderIdKey, folderId.value)
                    })
                this.respond(
                    content = ByteReadChannel(com.tomaszezula.makker.common.Scenario.response),
                    status = HttpStatusCode.OK,
                    headers = headersOf(
                        HttpHeaders.Authorization to listOf("Token ${token.value}")
                    )
                )
            }
            assertResult(
                makeAdapter(engine).createScenario(
                    blueprint,
                    scheduling,
                    CreateScenarioContext(token, teamId, folderId)
                ),
                com.tomaszezula.makker.common.Scenario.expected
            )
        }
    }

    @Test
    fun updateScenario() {
        runBlocking {
            val engine = MockEngine { requestData ->
                assertRequest(
                    requestData,
                    "${config.baseUrl}/scenarios/${scenarioId.value}?confirmed=true",
                    HttpMethod.Patch,
                    buildJsonObject {
                        put(BlueprintKey, blueprint.value.lineSequence().map { it.trim() }.joinToString(Separator))
                    })
                this.respond(
                    content = ByteReadChannel(com.tomaszezula.makker.common.Scenario.response),
                    status = HttpStatusCode.OK,
                    headers = headersOf()
                )
            }
            assertResult(
                makeAdapter(engine).updateScenario(blueprint, UpdateScenarioContext(token, scenarioId)),
                com.tomaszezula.makker.common.Scenario.expected
            )
        }
    }

    @Test
    fun getBlueprint() {
        runBlocking {
            val engine = MockEngine { requestData ->
                assertRequest(
                    requestData,
                    "${config.baseUrl}/scenarios/${scenarioId.value}/blueprint",
                    HttpMethod.Get
                )
                this.respond(
                    content = ByteReadChannel(com.tomaszezula.makker.common.Blueprint.response),
                    status = HttpStatusCode.OK,
                    headers = headersOf()
                )
            }
            assertResult(
                makeAdapter(engine).getBlueprint(scenarioId, token),
                com.tomaszezula.makker.common.Blueprint.expected
            )
        }
    }

    private fun makeAdapter(engine: MockEngine): MakeAdapter {
        return DefaultMakeAdapter(
            config,
            HttpClient(engine),
            Json {
                ignoreUnknownKeys = true
            }
        )
    }

    private fun assertRequest(requestData: HttpRequestData, url: String, method: HttpMethod, body: JsonObject? = null) {
        assertEquals(Url(url), requestData.url)
        assertTrue(requestData.headers.contains(HttpHeaders.Authorization, "Token ${token.value}"))
        assertEquals(requestData.method, method)
        body?.let { expectedBody ->
            assertEquals(requestData.body.contentType, ContentType.Application.Json)
            if (requestData.body is TextContent) {
                assertEquals(expectedBody.toString(), (requestData.body as TextContent).text)
            } else {
                fail("Unexpected body type:${requestData.body}")
            }
        }
    }

    private fun <T> assertResult(result: Result<T>, expected: T) {
        result.map {
            assertEquals(expected, it)
        }.onFailure {
            fail("Test failed", it)
        }
    }
}