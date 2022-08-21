package com.tomaszezula.makker.common.extensions

import com.tomaszezula.makker.common.toResult
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ToResultTest : StringSpec() {
    init {
        "toResult should succeed when there are no failures" {
            listOf(
                Result.success(1),
                Result.success(2),
                Result.success(3)
            ).toResult() shouldBe Result.success(listOf(1, 2, 3))
        }
        "toResult should fail if there is even a single failure" {
            listOf(
                Result.success(1),
                Result.success(2),
                Result.failure(NumberFormatException())
            ).toResult().isFailure shouldBe true

        }
        "toResult should ignore nulls" {
            listOf(
                Result.success(1),
                Result.success(null),
                Result.success(3)
            ).toResult() shouldBe Result.success(listOf(1, 3))
        }
    }
}
