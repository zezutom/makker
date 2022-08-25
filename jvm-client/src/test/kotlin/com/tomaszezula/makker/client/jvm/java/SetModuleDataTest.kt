package com.tomaszezula.makker.client.jvm.java

import com.tomaszezula.makker.client.jvm.MakeClient
import com.tomaszezula.makker.client.jvm.model.ModuleUpdate
import com.tomaszezula.makker.client.jvm.module
import com.tomaszezula.makker.client.jvm.scenario
import com.tomaszezula.makker.common.model.Blueprint
import com.tomaszezula.makker.common.model.UpdateResult
import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.common.runBlocking
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class SetModuleDataTest : StringSpec() {
    init {
        val kotlinClient = mockk<MakeClient>()
        val makeClient = DefaultMakeClient(kotlinClient)

        val key = "field1"
        val value = "hello world"

        this.coroutineTestScope = true

        "Set module data should succeed" {
            every {
                runBlocking {
                    kotlinClient.setModuleData(scenario.id, module.id, key, value)
                }
            } returns Result.success(UpdateResult.Success)
            makeClient.setModuleData(
                scenario.id.value,
                module.id.value,
                key,
                value
            ).orThrow shouldBe UpdateResult.Success
            verify(exactly = 1) {
                runBlocking {
                    kotlinClient.setModuleData(scenario.id, module.id, key, value)
                }
            }
        }
        "Set module data should respect the result returned by the underlying Kotlin client" {
            every {
                runBlocking {
                    kotlinClient.setModuleData(scenario.id, module.id, key, value)
                }
            } returns Result.success(UpdateResult.Failure)
            makeClient.setModuleData(
                scenario.id.value,
                module.id.value,
                key,
                value
            ).orThrow shouldBe UpdateResult.Failure
        }
        "Set module data should fail when the underlying Kotlin client fails" {
            val throwable = IllegalStateException("Something went wrong!")
            every {
                runBlocking {
                    kotlinClient.setModuleData(scenario.id, module.id, key, value)
                }
            } returns Result.failure(throwable)
            shouldThrowExactly<RuntimeException> {
                makeClient.setModuleData(
                    scenario.id.value,
                    module.id.value,
                    key,
                    value
                ).onFailure {
                    it shouldBe throwable
                }.onSuccess {
                    fail("The operation should have failed, but it was successful instead: $it")
                }.orThrow
            }
        }
        "Set module data should handle bulk changes" {
            val moduleUpdates = listOf(
                ModuleUpdate(Blueprint.Module.Id(1), "field1", "data1"),
                ModuleUpdate(Blueprint.Module.Id(2), "field2", "data2"),
                ModuleUpdate(Blueprint.Module.Id(3), "field3", "data3")
            )
            every {
                runBlocking {
                    kotlinClient.setModuleData(scenario.id, moduleUpdates)
                }
            } returns Result.success(UpdateResult.Success)
            makeClient.setModuleData(
                scenario.id.value,
                moduleUpdates.map { it.toModel() }
            ).orThrow shouldBe UpdateResult.Success

            verify(exactly = 1) {
                runBlocking {
                    kotlinClient.setModuleData(scenario.id, moduleUpdates)
                }
            }
        }
        "Set module data in bulk should respect the result returned by the underlying Kotlin client" {
            every {
                runBlocking {
                    kotlinClient.setModuleData(scenario.id, emptyList())
                }
            } returns Result.success(UpdateResult.Failure)
            makeClient.setModuleData(scenario.id.value, emptyList()).orThrow shouldBe UpdateResult.Failure
        }

        "Set module data in bulk should fail when the underlying Kotlin client fails" {
            val throwable = IllegalStateException("Something went wrong!")
            every {
                runBlocking {
                    kotlinClient.setModuleData(scenario.id, emptyList())
                }
            } returns Result.failure(throwable)
            shouldThrowExactly<RuntimeException> {
                makeClient.setModuleData(scenario.id.value, emptyList())
                    .onFailure {
                        it shouldBe throwable
                    }
                    .onSuccess {
                        fail("The operation should have failed, but it was successful instead: $it")
                    }.orThrow
            }
        }
    }
}