package com.tomaszezula.makker.server

import com.tomaszezula.makker.common.MakeAdapter
import com.tomaszezula.makker.common.model.Blueprint
import com.tomaszezula.makker.common.model.Scenario
import com.tomaszezula.makker.server.model.RequestContext
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class GetBlueprintV1Test {

    @Test
    fun success() {
        asAnonymous { getScenario(it) }
    }

    @Test
    fun successAuthenticated() {
        asAuthenticated { getScenario(it) }
    }

    @Test
    fun failure() {
        asAnonymous { getScenario(it, false) }
    }

    @Test
    fun failureAuthenticated() {
        asAuthenticated { getScenario(it, false) }
    }

    private fun getScenario(requestContext: RequestContext, shouldSucceed: Boolean = true) = testApplication {
        val makeAdapter = mockk<MakeAdapter>()
        val scenarioId = Scenario.Id(1)
        val blueprint = Blueprint("Test blueprint", scenarioId, emptyList(), Blueprint.Json("{}"))
        val result = result(blueprint, shouldSucceed)
        every {
            runBlocking {
                makeAdapter.getBlueprint(scenarioId, authToken)
            }
        } returns result
        withApplication(makeAdapter, requestContext)
        val response = client.get("/v1/scenarios/${scenarioId.value}/blueprint") {
            setHeaders()
        }
        if (shouldSucceed) {
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals(
                """
               {"name":"Test blueprint","scenarioId":1,"modules":[],"json":{"value":"{}","encoded":false}}
            """.trim(),
                response.bodyAsText()
            )
        } else {
            assertFailure(response, result)
        }
    }
}