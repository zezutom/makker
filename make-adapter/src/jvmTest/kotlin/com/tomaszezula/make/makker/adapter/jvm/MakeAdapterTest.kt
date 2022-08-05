package com.tomaszezula.make.makker.adapter.jvm

import com.tomaszezula.makker.adapter.MakeAdapter
import com.tomaszezula.makker.adapter.MakeAdapterImpl
import com.tomaszezula.makker.adapter.MakeConfig
import com.tomaszezula.makker.adapter.model.AuthToken
import com.tomaszezula.makker.adapter.model.Blueprint
import com.tomaszezula.makker.adapter.model.IndefiniteScheduling
import com.tomaszezula.makker.adapter.model.Scenario
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class MakeAdapterTest {

    companion object {
        val TeamId = Scenario.TeamId(1)
        val FolderId = Scenario.FolderId(1)
        val BlueprintJson = Blueprint.Json("{}")
        val Scheduling = IndefiniteScheduling()
    }

    private val config = MakeConfig(Url("https://test-server.com"))

    @Test
    fun createScenario() {
        runBlocking {
            val engine = MockEngine {
                assertEquals(Url("${config.baseUrl}/scenarios?confirmed=true"), it.url)
                this.respond(
                    content = ByteReadChannel(com.tomaszezula.make.makker.adapter.jvm.Scenario.data),
                    status = HttpStatusCode.OK,
                    headers = headersOf()
                )
            }
            assertResult(
                makeAdapter(engine).createScenario(TeamId, FolderId, BlueprintJson, Scheduling),
                com.tomaszezula.make.makker.adapter.jvm.Scenario.expected
            )
        }
    }

    private fun makeAdapter(engine: MockEngine): MakeAdapter =
        MakeAdapterImpl(
            AuthToken("test-token"),
            config,
            HttpClient(engine),
            Json {
                ignoreUnknownKeys = true
            }
        )

    private fun <T> assertResult(result: Result<T>, expected: T) {
        result.map {
            assertEquals(expected, it)
        }.onFailure {
            fail("Test failed", it)
        }
    }
}
