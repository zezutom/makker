package com.tomaszezula.makker.common.java.result

import com.tomaszezula.makker.common.java.Result
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import java.util.concurrent.CompletableFuture

class OnFailureTest : StringSpec() {
    init {
        "It executes the provided callback" {
            val ex = NumberFormatException()
            val exceptions = mutableListOf<Throwable>()
            shouldThrowExactly<RuntimeException> {
                Result<Int>(CompletableFuture.failedFuture(ex)).onFailure {
                    exceptions.add(it)
                }.orThrow
            }
            exceptions shouldContain ex
            exceptions.size shouldBe 1
        }
        "It does not execute onSuccess callback" {
            val initialValue = 1
            var x = initialValue
            shouldThrowExactly<RuntimeException> {
                Result<Int>(CompletableFuture.failedFuture(NumberFormatException())).onSuccess {
                    x *= 2
                }.orThrow
            }
            x shouldBe initialValue
        }
    }
}