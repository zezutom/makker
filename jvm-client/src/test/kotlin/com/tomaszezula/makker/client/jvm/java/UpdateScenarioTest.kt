package com.tomaszezula.makker.client.jvm.java

import com.tomaszezula.makker.client.jvm.MakeClient
import com.tomaszezula.makker.client.jvm.blueprint
import com.tomaszezula.makker.client.jvm.scenario
import com.tomaszezula.makker.common.model.Blueprint
import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldThrowExactly
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
        val kotlinClient = mockk<MakeClient>()
        val makeClient = DefaultMakeClient(kotlinClient)
        val updatedScenario = scenario.copy(name = "Updated scenario")

        this.coroutineTestScope = true

        "Update scenario should return the updated scenario" {
            every {
                runBlocking {
                    kotlinClient.updateScenario(scenario.id, blueprint.json)
                }
            } returns Result.success(updatedScenario)
            makeClient.updateScenario(
                scenario.id.value,
                blueprint.json.value
            ).orThrow shouldBe updatedScenario.toModel()
        }
        "Update scenario should fail when the underlying Kotlin client fails" {
            val throwable = IllegalStateException("Something went wrong!")
            every {
                runBlocking {
                    kotlinClient.updateScenario(scenario.id, blueprint.json)
                }
            } returns Result.failure(throwable)
            shouldThrowExactly<RuntimeException> {
                makeClient.updateScenario(scenario.id.value, blueprint.json.value)
                    .onFailure {
                        it shouldBe throwable
                    }
                    .onSuccess {
                        fail("The operation should have failed, but it was successful instead: $it")
                    }.orThrow
            }
        }
        "Update scenario should accept a Base64-encoded blueprint" {
            val encodedJson = Blueprint.Json(
                Base64.getEncoder().encodeToString(blueprint.json.value.toByteArray()),
                encoded = true
            )
            every {
                runBlocking {
                    kotlinClient.updateScenario(scenario.id, encodedJson)
                }
            } returns Result.success(updatedScenario)
            makeClient.updateScenarioEncoded(
                scenario.id.value,
                encodedJson.value
            ).orThrow shouldBe updatedScenario.toModel()
        }
        "Update scenario should read the blueprint from a file" {
            val filePath = Path.of("blueprint.json")
            every {
                runBlocking {
                    kotlinClient.updateScenario(scenario.id, filePath)
                }
            } returns Result.success(updatedScenario)

            mockkStatic(Files::class)
            every { Files.readAllLines(filePath) } returns listOf(blueprint.json.value)

            makeClient.updateScenario(scenario.id.value, filePath).orThrow shouldBe updatedScenario.toModel()
        }
    }
}