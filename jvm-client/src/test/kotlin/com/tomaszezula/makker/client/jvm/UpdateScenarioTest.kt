package com.tomaszezula.makker.client.jvm

import com.tomaszezula.makker.common.MakeAdapter
import com.tomaszezula.makker.common.model.Blueprint
import com.tomaszezula.makker.common.model.UpdateScenarioContext
import io.kotest.assertions.fail
import io.kotest.common.runBlocking
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

class UpdateScenarioTest : StringSpec() {
    init {
        val makeAdapter = mockk<MakeAdapter>()
        val makeClient = DefaultMakeClient(makeAdapter, token)
        val updatedScenario = scenario.copy(name = "Updated scenario")

        this.coroutineTestScope = true

        "Update scenario should return the updated scenario" {
            every {
                runBlocking {
                    makeAdapter.updateScenario(blueprint.json, UpdateScenarioContext(token, scenario.id))
                }
            } returns Result.success(updatedScenario)

            makeClient.updateScenario(scenario.id, blueprint.json).map {
                it shouldBe updatedScenario
            }

            verify(exactly = 1) {
                runBlocking {
                    makeAdapter.updateScenario(blueprint.json, UpdateScenarioContext(token, scenario.id))
                }
            }
        }

        "Update scenario should fail when the underlying Make adapter fails" {
            val throwable = IllegalStateException("Something went wrong!")
            every {
                runBlocking {
                    makeAdapter.updateScenario(blueprint.json, UpdateScenarioContext(token, scenario.id))
                }
            } returns Result.failure(throwable)
            makeClient.updateScenario(scenario.id, blueprint.json)
                .onFailure {
                    it shouldBe throwable
                }
                .onSuccess {
                    fail("The operation should have failed, but it was successful instead: $it")
                }
        }

        "Update scenario should accept a Base64-encoded blueprint" {
            val encodedJson = Blueprint.Json(
                Base64.getEncoder().encodeToString(blueprint.json.value.toByteArray()),
                encoded = true
            )
            every {
                runBlocking {
                    makeAdapter.updateScenario(encodedJson, UpdateScenarioContext(token, scenario.id))
                }
            } returns Result.success(updatedScenario)

            makeClient.updateScenario(scenario.id, encodedJson).map {
                it shouldBe updatedScenario
            }
        }

        "Update scenario should read the blueprint from a file" {
            every {
                runBlocking {
                    makeAdapter.updateScenario(blueprint.json, UpdateScenarioContext(token, scenario.id))
                }
            } returns Result.success(updatedScenario)

            mockkStatic(Files::class)
            every { Files.readAllLines(any()) } returns listOf(blueprint.json.value)

            makeClient.updateScenario(scenario.id, Path.of("blueprint.json")).map {
                it shouldBe updatedScenario
            }
        }

    }
}