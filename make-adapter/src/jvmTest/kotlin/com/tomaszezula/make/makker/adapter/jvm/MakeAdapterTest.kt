package com.tomaszezula.make.makker.adapter.jvm

import com.tomaszezula.makker.adapter.MakeAdapter
import com.tomaszezula.makker.adapter.DefaultMakeAdapter
import com.tomaszezula.makker.adapter.MakeConfig
import com.tomaszezula.makker.adapter.model.AuthToken
import com.tomaszezula.makker.adapter.model.Blueprint
import com.tomaszezula.makker.adapter.model.IndefiniteScheduling
import com.tomaszezula.makker.adapter.model.Scenario
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import io.ktor.content.TextContent
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.*
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
        val moduleId = Blueprint.Module.Id(1)
        val teamId = Scenario.TeamId(1)
        val folderId = Scenario.FolderId(1)
        val blueprintJson = Blueprint.Json("{}")
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
                        put(BlueprintKey, blueprintJson.value.lineSequence().map { it.trim() }.joinToString(Separator))
                        put(SchedulingKey, scheduling.toJson())
                        put(TeamIdKey, teamId.value)
                        put(FolderIdKey, folderId.value)
                    })
                this.respond(
                    content = ByteReadChannel(com.tomaszezula.make.makker.adapter.jvm.Scenario.response),
                    status = HttpStatusCode.OK,
                    headers = headersOf(
                        HttpHeaders.Authorization to listOf("Token ${token.value}")
                    )
                )
            }
            assertResult(
                makeAdapter(engine).createScenario(teamId, folderId, blueprintJson, scheduling, token),
                com.tomaszezula.make.makker.adapter.jvm.Scenario.expected
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
                        put(BlueprintKey, blueprintJson.value.lineSequence().map { it.trim() }.joinToString(Separator))
                    })
                this.respond(
                    content = ByteReadChannel(com.tomaszezula.make.makker.adapter.jvm.Scenario.response),
                    status = HttpStatusCode.OK,
                    headers = headersOf()
                )
            }
            assertResult(
                makeAdapter(engine).updateScenario(scenarioId, blueprintJson, token),
                com.tomaszezula.make.makker.adapter.jvm.Scenario.expected
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
                    content = ByteReadChannel(com.tomaszezula.make.makker.adapter.jvm.Blueprint.response),
                    status = HttpStatusCode.OK,
                    headers = headersOf()
                )
            }
            assertResult(
                makeAdapter(engine).getBlueprint(scenarioId, token),
                com.tomaszezula.make.makker.adapter.jvm.Blueprint.expected
            )
        }
    }

    @Test
    fun setModuleData() {
        val fieldName = "someField"
        val data = "test"
        runBlocking {
            val engine = MockEngine { requestData ->
                assertRequest(
                    requestData,
                    "${config.baseUrl}/scenarios/$scenarioId/data",
                    HttpMethod.Put,
                    buildJsonObject {
                        put(moduleId.value.toString(), Json.encodeToJsonElement(buildJsonObject {
                            put(fieldName, data)
                        }))
                    })
                this.respond(
                    content = ByteReadChannel(Module.response),
                    status = HttpStatusCode.OK,
                    headers = headersOf()
                )
            }
            assertResult(
                makeAdapter(engine).setModuleData(scenarioId, moduleId, fieldName, data, token),
                Module.expected
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
