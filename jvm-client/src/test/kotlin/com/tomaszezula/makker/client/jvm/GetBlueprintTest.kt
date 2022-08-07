package com.tomaszezula.makker.client.jvm

import com.tomaszezula.makker.adapter.MakeAdapter
import com.tomaszezula.makker.adapter.model.Blueprint
import com.tomaszezula.makker.adapter.model.Scenario
import io.kotest.common.runBlocking
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class GetBlueprintTest : StringSpec() {
    init {
        val makeAdapter = mockk<MakeAdapter>()
        val makeClient = MakeClientImpl(makeAdapter)

        this.coroutineTestScope = true

        "Get blueprint should return the scenario blueprint" {
            every {
                runBlocking {
                    makeAdapter.getBlueprint(scenario.id)
                }
            } returns Result.success(blueprint)

            makeClient.getBlueprint(scenario.id).map {
                it shouldBe blueprint
            }
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
                        makeAdapter.getBlueprint(it.key)
                    }
                } returns Result.success(it.value)
            }
            makeClient.getBlueprints(blueprintMap.keys.toList()).map {
                it shouldBe blueprintMap.values.toList()
            }
        }
    }
}
