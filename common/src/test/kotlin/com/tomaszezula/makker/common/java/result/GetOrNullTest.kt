package com.tomaszezula.makker.common.java.result

import io.kotest.core.spec.style.StringSpec
import com.tomaszezula.makker.common.java.Result
import io.kotest.matchers.shouldBe
import java.util.concurrent.CompletableFuture

class GetOrNullTest : StringSpec() {
    init {
        "It should return a value when it is present" {
            val x = 1
            Result(CompletableFuture.completedFuture(x)).orNull shouldBe x
        }
        "It should return null when there is no value" {
            Result(CompletableFuture.completedFuture(null)).orNull shouldBe null
        }
        "It should return null when the result has failed" {
            Result(CompletableFuture.failedFuture<Int>(NumberFormatException())).orNull shouldBe null
        }
    }
}