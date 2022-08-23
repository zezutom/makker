package com.tomaszezula.makker.common.java.result

import com.tomaszezula.makker.common.java.Result
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.CompletableFuture

class OnSuccessTest : StringSpec() {
    init {
        "It executes the provided callback" {
            val initialValue = 1
            var x = initialValue
            val y = 1
            Result(CompletableFuture.completedFuture(y)).onSuccess {
                x += it
            }.orThrow shouldBe y
            x shouldBe initialValue + y
        }
        "It does not execute onFailure callback" {
            val initialValue = 0
            val x = 1
            var failCount = initialValue
            Result(CompletableFuture.completedFuture(x)).onFailure {
                failCount += 1
            }.orThrow shouldBe x
            failCount shouldBe initialValue
        }
    }
}