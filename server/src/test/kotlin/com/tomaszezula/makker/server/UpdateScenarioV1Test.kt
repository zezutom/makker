package com.tomaszezula.makker.server

import com.tomaszezula.makker.common.MakeAdapter
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

class UpdateScenarioV1Test {

    @Test
    fun success() {
        asAnonymous { updateScenario(it) }
    }

    @Test
    fun successAuthenticated() {
        asAuthenticated { updateScenario(it) }
    }

    @Test
    fun failure() {
        asAnonymous { updateScenario(it, false) }
    }

    @Test
    fun failureAuthenticated() {
        asAuthenticated { updateScenario(it, false) }
    }

    private fun updateScenario(requestContext: RequestContext, shouldSucceed: Boolean = true) = testApplication {
        val makeAdapter = mockk<MakeAdapter>()
        val scenario = Scenario(
            Scenario.Id(1),
            Scenario.TeamId(2),
            Scenario.FolderId(3),
            "Test scenario"
        )
        val result = result(scenario, shouldSucceed)
        every {
            runBlocking {
                makeAdapter.updateScenario(any(), any())
            }
        } returns result
        withApplication(makeAdapter, requestContext)
        val response = client.patch("/v1/scenarios/${scenario.id.value}") {
            setHeaders()
            setBody(buildJsonObject {
                put("blueprint", "{}")
            }.toString())
        }
        if (shouldSucceed) {
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals(
                """
                   {"id":${scenario.id.value},"teamId":${scenario.teamId.value},"folderId":${scenario.folderId!!.value},"name":"${scenario.name}"} 
                """.trim(),
                response.bodyAsText())
        } else {
            assertFailure(response, result)
        }
    }
}