package com.tomaszezula.makker.client.jvm

import com.tomaszezula.makker.adapter.MakeAdapter
import com.tomaszezula.makker.adapter.model.Blueprint
import com.tomaszezula.makker.adapter.model.IndefiniteScheduling
import com.tomaszezula.makker.adapter.model.Scenario
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
        val makeClient = MakeClientImpl(makeAdapter)

        val teamId = Scenario.TeamId(1)
        val folderId = Scenario.FolderId(1)
        val blueprintJson = Blueprint.Json("{}")
        val scheduling = IndefiniteScheduling()
        val scenario = Scenario(
            Scenario.Id(1),
            teamId,
            folderId,
            "New scenario"
        )

        this.coroutineTestScope = true

        "Create scenario should return the created scenario" {
            every {
                runBlocking {
                    makeAdapter.createScenario(
                        teamId,
                        folderId,
                        blueprintJson,
                        scheduling
                    )
                }
            } returns Result.success(scenario)

            makeClient.createScenario(teamId, folderId, blueprintJson, scheduling).map { createdScenario ->
                createdScenario shouldBe scenario
            }

            verify(exactly = 1) {
                runBlocking {
                    makeAdapter.createScenario(
                        teamId,
                        folderId,
                        blueprintJson,
                        scheduling
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
                        blueprintJson,
                        scheduling
                    )
                }
            } returns Result.failure(throwable)

            makeClient.createScenario(teamId, folderId, blueprintJson, scheduling)
                .onFailure {
                    it shouldBe throwable
                }
                .onSuccess {
                    fail("The operation should have failed, but it was successful instead: $it")
                }
        }

        "Create scenario should accept a Base64-encoded blueprint" {
            every {
                runBlocking {
                    makeAdapter.createScenario(
                        teamId,
                        folderId,
                        blueprintJson,
                        scheduling
                    )
                }
            } returns Result.success(scenario)

            val encodedJson = Blueprint.Json(Base64.getEncoder().encodeToString(blueprintJson.value.toByteArray()))
            makeClient.createScenario(teamId, folderId, encodedJson, scheduling, encoded = true).map { createdScenario ->
                createdScenario shouldBe scenario
            }
        }
        "Create scenario should read the blueprint from a file" {
            every {
                runBlocking {
                    makeAdapter.createScenario(
                        teamId,
                        folderId,
                        blueprintJson,
                        scheduling
                    )
                }
            } returns Result.success(scenario)

            mockkStatic(Files::class)
            every { Files.readAllLines(any())} returns listOf(blueprintJson.value)

            makeClient.createScenario(teamId, folderId, scheduling, Path.of("blueprint.json")).map { createdScenario ->
                createdScenario shouldBe scenario
            }
        }
    }
}