package com.tomaszezula.makker.client.jvm

import com.tomaszezula.makker.client.jvm.model.ModuleUpdate
import com.tomaszezula.makker.common.MakeAdapter
import com.tomaszezula.makker.common.model.Blueprint
import com.tomaszezula.makker.common.model.UpdateResult
import io.kotest.assertions.fail
import io.kotest.common.runBlocking
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class SetModuleDataTest : StringSpec() {
    init {
        val makeAdapter = mockk<MakeAdapter>()
        val makeClient = DefaultMakeClient(makeAdapter, token)

        val fieldName = "field1"
        val data = "hello world"

        this.coroutineTestScope = true

        "Set module data should succeed" {
            every {
                runBlocking {
                    makeAdapter.setModuleData(scenario.id, module.id, fieldName, data, token)
                }
            } returns Result.success(UpdateResult(true))
            makeClient.setModuleData(scenario.id, module.id, fieldName, data).map {
                it shouldBe UpdateResult.Success
            }
            verify(exactly = 1) {
                runBlocking {
                    makeAdapter.setModuleData(scenario.id, module.id, fieldName, data, token)
                }
            }
        }
        "Set module data should respect the result returned by the underlying Make adapter" {
            every {
                runBlocking {
                    makeAdapter.setModuleData(scenario.id, module.id, fieldName, data, token)
                }
            } returns Result.success(UpdateResult.Failure)
            makeClient.setModuleData(scenario.id, module.id, fieldName, data).map {
                it shouldBe UpdateResult.Failure
            }
        }
        "Set module data should fail when the underlying Make adapter fails" {
            val throwable = IllegalStateException("Something went wrong!")
            every {
                runBlocking {
                    makeAdapter.setModuleData(scenario.id, module.id, fieldName, data, token)
                }
            } returns Result.failure(throwable)
            makeClient.setModuleData(scenario.id, module.id, fieldName, data)
                .onFailure {
                    it shouldBe throwable
                }
                .onSuccess {
                    fail("The operation should have failed, but it was successful instead: $it")
                }
        }
        "Set module data should handle bulk changes" {
            val moduleUpdates = listOf(
                ModuleUpdate(Blueprint.Module.Id(1), "field1", "data1"),
                ModuleUpdate(Blueprint.Module.Id(2), "field2", "data2"),
                ModuleUpdate(Blueprint.Module.Id(3), "field3", "data3")
            )
            moduleUpdates.forEach {
                every {
                    runBlocking {
                        makeAdapter.setModuleData(scenario.id, it.moduleId, it.key, it.value, token)
                    }
                } returns Result.success(UpdateResult.Success)
            }
            makeClient.setModuleData(scenario.id, moduleUpdates).map {
                it shouldBe UpdateResult.Success
            }

            moduleUpdates.forEach {
                verify(exactly = 1) {
                    runBlocking {
                        makeAdapter.setModuleData(scenario.id, it.moduleId, it.key, it.value, token)
                    }
                }
            }
        }
        "Set module data should only return success when all operations are successful" {
            val moduleUpdateMap = mapOf(
                ModuleUpdate(Blueprint.Module.Id(1), "field1", "data1") to true,
                ModuleUpdate(Blueprint.Module.Id(2), "field2", "data2") to false,
                ModuleUpdate(Blueprint.Module.Id(3), "field3", "data3") to true
            )
            moduleUpdateMap.entries.forEach {
                every {
                    runBlocking {
                        makeAdapter.setModuleData(scenario.id, it.key.moduleId, it.key.key, it.key.value, token)
                    }
                } returns Result.success(UpdateResult(it.value))
            }
            makeClient.setModuleData(scenario.id, moduleUpdateMap.keys.toList()).map {
                it shouldBe UpdateResult.Failure
            }
        }
    }
}