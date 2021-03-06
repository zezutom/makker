package com.tomaszezula.makker.client

import com.tomaszezula.makker.client.MakeApi.blueprint
import com.tomaszezula.makker.client.MakeApi.scenario
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.junit.jupiter.api.fail

class MakeClientTest : WordSpec({

    "Create scenario" should {
        "submit a valid request to Make and handle a successful response" {
            withMakeClient(scenario()) { ctx ->
                ctx.makeClient.createScenario(
                    teamId,
                    folderId,
                    blueprint,
                    scheduling
                ).onSuccess { scenario ->
                    // Request
                    ctx.httpClient.shouldPost(
                        url = "${config.baseUrl}/scenarios?confirmed=true",
                        payload = buildJsonObject {
                            put(
                                DefaultMakeClient.Companion.CreateScenario.Params.Blueprint,
                                blueprint.value.lineSequence().map { it.trim() }.joinToString("")
                            )
                            put(
                                DefaultMakeClient.Companion.CreateScenario.Params.Scheduling,
                                scheduling.toJson()
                            )
                            put(DefaultMakeClient.Companion.CreateScenario.Params.TeamId, teamId.value)
                            put(DefaultMakeClient.Companion.CreateScenario.Params.FolderId, folderId.value)
                        })
                    // Response
                    scenario.id.value shouldBe ctx.response.scenario.id
                    scenario.name shouldBe ctx.response.scenario.name
                }.onFailure {
                    fail(it)
                }
            }
        }
    }
    "Get blueprint" should {
        "submit a valid request to Make and handle a successful response" {
            withMakeClient(blueprint()) { ctx ->
                ctx.makeClient.getBlueprint(scenarioId)
                    .onSuccess { blueprint ->
                        // Request
                        ctx.httpClient.shouldGet("${config.baseUrl}/scenarios/${scenarioId.value}/blueprint")
                        // Response
                        blueprint.name shouldBe ctx.response.response.blueprint.name
                        blueprint.modules.size shouldBeGreaterThan 0
                        blueprint.modules shouldBe ctx.response.response.blueprint.flow.mapNotNull { it.toModule() }
                        blueprint.json.value shouldBe mapper.writeValueAsString(ctx.response)
                    }.onFailure {
                        fail(it)
                    }
            }
        }
    }
    "Set module data" should {
        "submit a valid request to Make and handle a successful response" {
            val fieldName = "fruit"
            val model = mapOf(
                fieldName to "apples"
            )
            val updatedValue = "bananas"
            val data = mapper.readTree("""{"content": "$updatedValue"}""")
            withMakeClient(blueprint(model)) { ctx ->
                ctx.makeClient.setModuleData(
                    scenarioId,
                    moduleId,
                    fieldName,
                    data
                ).onSuccess { module ->
                    // Request
                    ctx.httpClient.shouldPatch("${config.baseUrl}/scenarios/${scenarioId.value}?confirmed=true")
                    // Response
                    ctx.response.response.blueprint.flow.size shouldBe 1
                    val flow = ctx.response.response.blueprint.flow.first()
                    module.id.value shouldBe flow.id
                    module.name shouldBe flow.module.toString()
                    module.model shouldContainKey fieldName
                    module.model[fieldName] shouldBe updatedValue
                }.onFailure {
                    fail(it)
                }
            }
        }
    }
})

