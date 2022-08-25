package com.tomaszezula.makker.client.jvm.java

import com.tomaszezula.makker.client.jvm.MakeClient
import com.tomaszezula.makker.client.jvm.blueprint
import com.tomaszezula.makker.client.jvm.scenario
import com.tomaszezula.makker.common.model.Blueprint
import com.tomaszezula.makker.common.model.Scenario
import io.kotest.common.runBlocking
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class GetBlueprintTest : StringSpec() {
    init {
        val kotlinClient = mockk<MakeClient>()
        val makeClient = DefaultMakeClient(kotlinClient)

        this.coroutineTestScope = true

        "Get blueprint should return the scenario blueprint" {
            every {
                runBlocking {
                    kotlinClient.getBlueprint(scenario.id)
                }
            } returns Result.success(blueprint)

            makeClient.getBlueprint(scenario.id.value).orThrow shouldBe blueprint
        }
        "Get blueprints should return a blueprint for each scenario" {
            val blueprintMap = mapOf(
                Scenario.Id(10) to blueprint.copy(json = Blueprint.Json("{\"name\": \"one\"}")),
                Scenario.Id(20) to blueprint.copy(json = Blueprint.Json("{\"name\": \"two\"}")),
                Scenario.Id(30) to blueprint.copy(json = Blueprint.Json("{\"name\": \"three\"}"))
            )
            blueprintMap.entries.forEach {
                every {
                    runBlocking {
                        kotlinClient.getBlueprint(it.key)
                    }
                } returns Result.success(it.value)
            }
            makeClient.getBlueprints(blueprintMap.keys.map { it.value }).onSuccess {
                it shouldBe blueprintMap.values.toList()
            }
            verify(exactly = 1) {
                runBlocking {
                    kotlinClient.getBlueprints(blueprintMap.keys.toList())
                }
            }
        }
    }
}