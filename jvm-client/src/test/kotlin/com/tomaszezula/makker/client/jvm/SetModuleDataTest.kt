package com.tomaszezula.makker.client.jvm

import com.tomaszezula.makker.common.MakeAdapter
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

        "Set module data should return a flag" {
            every {
                runBlocking {
                    makeAdapter.setModuleData(scenario.id, module.id, fieldName, data, token)
                }
            } returns Result.success(true)
            makeClient.setModuleData(scenario.id, module.id, fieldName, data).map {
                it shouldBe true
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
            } returns Result.success(false)
            makeClient.setModuleData(scenario.id, module.id, fieldName, data).map {
                it shouldBe false
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
            val fieldMap = mapOf(
                "field1" to "data1",
                "field2" to "data2",
                "field3" to "data3"
            )
            fieldMap.entries.forEach {
                every {
                    runBlocking {
                        makeAdapter.setModuleData(scenario.id, module.id, it.key, it.value, token)
                    }
                } returns Result.success(true)
            }
            makeClient.setModuleData(scenario.id, module.id, fieldMap).map {
                it shouldBe true
            }
            fieldMap.entries.forEach {
                verify(exactly = 1) {
                    runBlocking {
                        makeAdapter.setModuleData(scenario.id, module.id, it.key, it.value, token)
                    }
                }
            }
        }
        "Set module data should only return success when all operations are successful" {
            val fieldMap = mapOf(
                "field1" to Pair("data1", true),
                "field2" to Pair("data2", false),
                "field3" to Pair("data3", true)
            )
            fieldMap.entries.forEach {
                every {
                    runBlocking {
                        makeAdapter.setModuleData(scenario.id, module.id, it.key, it.value.first, token)
                    }
                } returns Result.success(it.value.second)
            }
            makeClient.setModuleData(scenario.id, module.id, fieldMap.map { it.key to it.value.first }.toMap()).map {
                it shouldBe false
            }
        }
    }
}