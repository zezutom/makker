package com.tomaszezula.makker.client

import com.tomaszezula.makke.api.Scenario__1
import com.tomaszezula.makke.api.Scenario__2
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.ktor.client.*
import io.ktor.http.*
import io.ktor.http.content.*
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.junit.jupiter.api.fail

class MakeClientTest : FunSpec({

    fun makeClient(httpClient: HttpClient): MakeClient = DefaultMakeClient(httpClient, mapper, config)

    fun makeScenario(): Scenario__1 {
        val makeResponse = Scenario__1()
        val scenario = Scenario__2()
        scenario.id = 1
        scenario.name = "New Scenario"
        makeResponse.scenario = scenario
        return makeResponse
    }

    test("create scenario") {
        val makeScenario = makeScenario()
        val httpClient = httpClient(makeScenario)
        val makeClient = makeClient(httpClient)
        makeClient.createScenario(
            teamId,
            folderId,
            blueprint,
            scheduling
        ).onSuccess { scenario ->
            // Request
            val requestHistory = httpClient.mockEngine().requestHistory
            requestHistory shouldHaveSize 1

            val requestData = requestHistory.first()
            requestData.url.toString() shouldBe "${config.baseUrl}/scenarios?confirmed=true"
            requestData.method shouldBe HttpMethod.Post
            requestData.headers.contains(HttpHeaders.Authorization, "Token ${config.token.value}") shouldBe true

            when (val body = requestData.body) {
                is TextContent -> {
                    body.contentType shouldBe ContentType.Application.Json
                    body.text shouldBe buildJsonObject {
                        put(
                            DefaultMakeClient.Companion.CreateScenario.Params.Blueprint,
                            blueprint.value.lineSequence().map { it.trim() }.joinToString("")
                        )
                        put(DefaultMakeClient.Companion.CreateScenario.Params.Scheduling, scheduling.toJson())
                        put(DefaultMakeClient.Companion.CreateScenario.Params.TeamId, teamId.value)
                        put(DefaultMakeClient.Companion.CreateScenario.Params.FolderId, folderId.value)
                    }.toString()
                }
                else -> fail("Unexpected request payload: $body")
            }
            // Response
            scenario.id.value shouldBe makeScenario.scenario.id
            scenario.name shouldBe makeScenario.scenario.name
        }.onFailure {
            fail(it)
        }
    }

})

