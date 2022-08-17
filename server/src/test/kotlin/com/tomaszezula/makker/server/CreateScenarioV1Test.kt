package com.tomaszezula.makker.server

import com.tomaszezula.makker.common.MakeAdapter
import com.tomaszezula.makker.common.model.Blueprint
import com.tomaszezula.makker.common.model.CreateScenarioContext
import com.tomaszezula.makker.common.model.Scenario
import com.tomaszezula.makker.server.model.RequestContext
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.test.Test
import kotlin.test.assertEquals

class CreateScenarioV1Test {

    @Test
    fun success() {
        asAnonymous { createScenario(it) }
    }

    @Test
    fun successAuthenticated() {
        asAuthenticated { createScenario(it) }
    }

    @Test
    fun failure() {
        asAnonymous { createScenario(it, false) }
    }

    @Test
    fun failureAuthenticated() {
        asAuthenticated { createScenario(it, false) }
    }

    private fun createScenario(requestContext: RequestContext, shouldSucceed: Boolean = true) =
        testApplication {
            val makeAdapter = mockk<MakeAdapter>()
            val scenario = Scenario(
                Scenario.Id(1),
                Scenario.TeamId(2),
                Scenario.FolderId(3),
                "Test scenario"
            )
            val blueprint = Blueprint.Json("{}")
            val result = result(scenario, shouldSucceed)
            every {
                runBlocking {
                    makeAdapter.createScenario(
                        blueprint,
                        any(),
                        CreateScenarioContext(authToken, scenario.teamId, scenario.folderId!!)
                    )
                }
            } returns result

            withApplication(makeAdapter, requestContext)
            val response = client.post("/v1/scenarios") {
                setHeaders()
                setBody(buildJsonObject {
                    put("teamId", scenario.teamId.value)
                    put("folderId", scenario.folderId!!.value)
                    put("blueprint", "{}")
                }.toString())
            }
            if (shouldSucceed) {
                assertEquals(HttpStatusCode.Created, response.status)
                assertEquals(
                    """
                   {"id":${scenario.id.value},"teamId":${scenario.teamId.value},"folderId":${scenario.folderId!!.value},"name":"${scenario.name}"} 
                """.trim(),
                    response.bodyAsText()
                )
            } else {
                assertFailure(response, result)
            }
        }
}