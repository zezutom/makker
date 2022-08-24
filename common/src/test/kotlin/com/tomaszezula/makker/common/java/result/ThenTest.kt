package com.tomaszezula.makker.common.java.result

import io.kotest.core.spec.style.StringSpec
import com.tomaszezula.makker.common.java.Result
import io.kotest.matchers.shouldBe
import java.util.concurrent.CompletableFuture

class ThenTest : StringSpec() {
    init {
        "It should map A to B" {
            val x = 1
            val multiplier = 2
            Result(CompletableFuture.completedFuture(x))
                .then {
                    Result(CompletableFuture.completedFuture(it * multiplier))
                }
                .then {
                    Result(CompletableFuture.completedFuture(it.toString()))
                }.orThrow shouldBe (x * multiplier).toString()
        }
    }
}