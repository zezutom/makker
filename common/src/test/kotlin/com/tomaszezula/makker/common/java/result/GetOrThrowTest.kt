package com.tomaszezula.makker.common.java.result

import io.kotest.core.spec.style.StringSpec
import com.tomaszezula.makker.common.java.Result
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.shouldBe
import java.util.concurrent.CompletableFuture

class GetOrThrowTest : StringSpec() {
    init {
        "It should return a value when it is present" {
            val x = 1
            Result(CompletableFuture.completedFuture(x)).orThrow shouldBe x
        }
        "It should throw an exception when there is no value" {
            shouldThrowExactly<RuntimeException> {
                Result(CompletableFuture.completedFuture(null)).orThrow
            }
        }
        "It should throw an exception when the result has failed" {
            shouldThrowExactly<RuntimeException> {
                Result(CompletableFuture.failedFuture<Int>(NumberFormatException())).orThrow
            }
        }
    }
}