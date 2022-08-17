package com.tomaszezula.makker.server

import com.tomaszezula.makker.common.MakeAdapter
import com.tomaszezula.makker.common.model.Blueprint
import com.tomaszezula.makker.common.model.Scenario
import com.tomaszezula.makker.common.model.SetModuleDataContext
import com.tomaszezula.makker.common.model.UpdateResult
import com.tomaszezula.makker.server.model.RequestContext
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.test.Test
import kotlin.test.assertEquals

class SetModuleDataV1Test {

    @Test
    fun success() {
        asAnonymous { setModuleData(it) }
    }

    @Test
    fun successAuthenticated() {
        asAuthenticated { setModuleData(it) }
    }

    @Test
    fun failure() {
        asAnonymous { setModuleData(it, false) }
    }

    @Test
    fun failureAuthenticated() {
        asAuthenticated { setModuleData(it, false) }
    }

    private fun setModuleData(requestContext: RequestContext, shouldSucceed: Boolean = true) = testApplication {
        val makeAdapter = mockk<MakeAdapter>()
        val scenarioId = Scenario.Id(1)
        val moduleId = Blueprint.Module.Id(1)
        val key = "json"
        val value = "{}"
        val result = result(UpdateResult.Success, shouldSucceed)
        every {
            runBlocking {
                makeAdapter.setModuleData(key, value, SetModuleDataContext(authToken, scenarioId, moduleId))
            }
        } returns result

        withApplication(makeAdapter, requestContext)
        val response = client.put("/v1/scenarios/${scenarioId.value}/data") {
            setHeaders()
            setBody(buildJsonObject {
                put("modules", buildJsonArray {
                    add(buildJsonObject {
                        put("moduleId", moduleId.value)
                        put("key", key)
                        put("value", value)
                    })
                })
            }.toString())
        }
        if (shouldSucceed) {
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals(
                """
                   {"result":true} 
                """.trim(),
                response.bodyAsText()
            )
        } else {
            assertFailure(response, result, "Operation failed for one or more fields.")
        }
    }
}