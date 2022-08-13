package com.tomaszezula.makker.client.jvm

import com.tomaszezula.makker.common.MakeAdapter
import com.tomaszezula.makker.common.model.Blueprint
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

class CreateScenarioTest : StringSpec() {
    init {
        val makeAdapter = mockk<MakeAdapter>()
        val makeClient: MakeClient = DefaultMakeClient(makeAdapter, token)

        this.coroutineTestScope = true

        "Create scenario should return the created scenario" {
            every {
                runBlocking {
                    makeAdapter.createScenario(
                        teamId,
                        folderId,
                        blueprint.json,
                        scheduling,
                        token
                    )
                }
            } returns Result.success(scenario)

            makeClient.createScenario(teamId, folderId, blueprint.json, scheduling).map {
                it shouldBe scenario
            }

            verify(exactly = 1) {
                runBlocking {
                    makeAdapter.createScenario(
                        teamId,
                        folderId,
                        blueprint.json,
                        scheduling,
                        token
                    )
                }
            }
        }

        "Create scenario should fail when the underlying Make adapter fails" {
            val throwable = IllegalStateException("Something went wrong!")
            every {
                runBlocking {
                    makeAdapter.createScenario(
                        teamId,
                        folderId,
                        blueprint.json,
                        scheduling,
                        token
                    )
                }
            } returns Result.failure(throwable)

            makeClient.createScenario(teamId, folderId, blueprint.json, scheduling)
                .onFailure {
                    it shouldBe throwable
                }
                .onSuccess {
                    fail("The operation should have failed, but it was successful instead: $it")
                }
        }

        "Create scenario should accept a Base64-encoded blueprint" {
            val encodedJson = Blueprint.Json(
                Base64.getEncoder().encodeToString(blueprint.json.value.toByteArray()),
                encoded = true
            )
            every {
                runBlocking {
                    makeAdapter.createScenario(
                        teamId,
                        folderId,
                        encodedJson,
                        scheduling,
                        token
                    )
                }
            } returns Result.success(scenario)

            makeClient.createScenario(teamId, folderId, encodedJson, scheduling).map {
                it shouldBe scenario
            }
        }

        "Create scenario should read the blueprint from a file" {
            every {
                runBlocking {
                    makeAdapter.createScenario(
                        teamId,
                        folderId,
                        blueprint.json,
                        scheduling,
                        token
                    )
                }
            } returns Result.success(scenario)

            mockkStatic(Files::class)
            every { Files.readAllLines(any()) } returns listOf(blueprint.json.value)

            makeClient.createScenario(teamId, folderId, Path.of("blueprint.json"), scheduling).map {
                it shouldBe scenario
            }
        }
    }
}