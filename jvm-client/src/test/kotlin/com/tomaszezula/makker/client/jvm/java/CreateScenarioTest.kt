package com.tomaszezula.makker.client.jvm.java

import com.tomaszezula.makker.client.jvm.*
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

class CreateScenarioTest : StringSpec() {
    init {
        val kotlinClient = mockk<com.tomaszezula.makker.client.jvm.MakeClient>()
        val makeClient = DefaultMakeClient(kotlinClient)

        this.coroutineTestScope = true

        "Create scenario should return the created scenario" {
            every {
                runBlocking {
                    kotlinClient.createScenario(
                        teamId,
                        folderId,
                        blueprint.json,
                        scheduling
                    )
                }
            } returns Result.success(scenario)

            makeClient.createScenario(
                teamId.value,
                folderId.value,
                blueprint.json.value
            ).orThrow shouldBe scenario.toModel()
        }

        "Create scenario should fail when the underlying Make client fails" {
            val throwable = IllegalStateException("Something went wrong!")
            every {
                runBlocking {
                    kotlinClient.createScenario(
                        teamId,
                        folderId,
                        blueprint.json,
                        scheduling
                    )
                }
            } returns Result.failure(throwable)

            shouldThrowExactly<RuntimeException> {
                makeClient.createScenario(
                    teamId.value,
                    folderId.value,
                    blueprint.json.value
                ).onFailure {
                    it shouldBe throwable
                }.onSuccess {
                    fail("The operation should have failed, but it was successful instead: $it")
                }.orThrow
            }
        }
        "Create scenario should accept a Base64-encoded blueprint" {
            val encodedJson = Blueprint.Json(
                Base64.getEncoder().encodeToString(blueprint.json.value.toByteArray()),
                encoded = true
            )
            every {
                runBlocking {
                    kotlinClient.createScenario(
                        teamId,
                        folderId,
                        encodedJson,
                        scheduling
                    )
                }
            } returns Result.success(scenario)

            makeClient.createScenarioEncoded(
                teamId.value,
                folderId.value,
                encodedJson.value,
            ).orThrow shouldBe scenario.toModel()
        }
        "Create scenario should read the blueprint from a file" {
            val filePath = Path.of("blueprint.json")
            every {
                runBlocking {
                    kotlinClient.createScenario(
                        teamId,
                        folderId,
                        filePath,
                        scheduling
                    )
                }
            } returns Result.success(scenario)

            mockkStatic(Files::class)
            every { Files.readAllLines(filePath) } returns listOf(blueprint.json.value)

            makeClient.createScenario(
                teamId.value,
                folderId.value,
                filePath
            ).orThrow shouldBe scenario.toModel()
        }
    }
}