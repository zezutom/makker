package com.tomaszezula.makker.client.jvm

import com.tomaszezula.makker.adapter.MakeAdapter
import com.tomaszezula.makker.adapter.model.Blueprint
import io.kotest.assertions.fail
import io.kotest.common.runBlocking
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

class UpdateScenarioTest : StringSpec() {
    init {
        val makeAdapter = mockk<MakeAdapter>()
        val makeClient = MakeClientImpl(makeAdapter)
        val updatedScenario = scenario.copy(name = "Updated scenario")

        this.coroutineTestScope = true

        "Update scenario should return the updated scenario" {
            every {
                runBlocking {
                    makeAdapter.updateScenario(scenario.id, blueprintJson)
                }
            } returns Result.success(updatedScenario)

            makeClient.updateScenario(scenario.id, blueprintJson).map { scenario ->
                scenario shouldBe updatedScenario
            }
        }

        "Update scenario should fail when the underlying Make adapter fails" {
            val throwable = IllegalStateException("Something went wrong!")
            every {
                runBlocking {
                    makeAdapter.updateScenario(scenario.id, blueprintJson)
                }
            } returns Result.failure(throwable)
            makeClient.updateScenario(scenario.id, blueprintJson)
                .onFailure {
                    it shouldBe throwable
                }
                .onSuccess {
                    fail("The operation should have failed, but it was successful instead: $it")
                }
        }

        "Update scenario should accept a Base64-encoded blueprint" {
            every {
                runBlocking {
                    makeAdapter.updateScenario(scenario.id, blueprintJson)
                }
            } returns Result.success(updatedScenario)

            val encodedJson = Blueprint.Json(Base64.getEncoder().encodeToString(blueprintJson.value.toByteArray()))
            makeClient.updateScenario(scenario.id, encodedJson, encoded = true).map { scenario ->
                scenario shouldBe updatedScenario
            }
        }

        "Update scenario should read the blueprint from a file" {
            every {
                runBlocking {
                    makeAdapter.updateScenario(scenario.id, blueprintJson)
                }
            } returns Result.success(updatedScenario)

            mockkStatic(Files::class)
            every { Files.readAllLines(any())} returns listOf(blueprintJson.value)

            makeClient.updateScenario(scenario.id, Path.of("blueprint.json")).map { scenario ->
                scenario shouldBe updatedScenario
            }
        }

    }
}