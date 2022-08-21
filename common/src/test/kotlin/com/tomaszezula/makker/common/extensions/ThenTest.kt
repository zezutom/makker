package com.tomaszezula.makker.common.extensions

import com.tomaszezula.makker.common.then
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.shouldBe

class ThenTest : StringSpec() {
    init {
        "returns the computed result" {
            Result.success(1).then { Result.success("$it") } shouldBe Result.success("1")
        }
        "can be chained" {
            Result.success(listOf(1, 2, 3))
                .then { Result.success(it.filter { x -> x != 2 }) }
                .then { Result.success(it.map { x -> x * 2 }) }
                .then { Result.success(it.joinToString()) } shouldBe Result.success("2, 6")
        }
        "gracefully handles exceptions" {
            val ex = NumberFormatException()
            Result.failure<Int>(ex).then { Result.success(it.toString()) } shouldBeFailure {
                it shouldBe ex
            }
        }
        "gracefully handles nested exceptions" {
            val ex = NumberFormatException()
            Result.success(listOf("1", "two", "3")).then { Result.failure<Int>(ex) } shouldBeFailure {
                it shouldBe ex
            }
        }
    }
}