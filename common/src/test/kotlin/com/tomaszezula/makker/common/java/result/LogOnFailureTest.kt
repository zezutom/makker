package com.tomaszezula.makker.common.java.result

import com.tomaszezula.makker.common.java.Result
import io.kotest.core.spec.style.StringSpec
import io.mockk.verify
import java.util.concurrent.CompletableFuture

class LogOnFailureTest : StringSpec() {
    init {
        "It executes the provided callback" {
            val ex = NumberFormatException()
            val logger = loggerMock(ex)
            Result(CompletableFuture.failedFuture<Int>(NumberFormatException())).logOnFailure {
                logger.warn(it.toString())
            }
            verify(exactly = 1) {
                logger.warn(ex.toString())
            }
        }
        "It does not execute the provided callback on success" {
            val logger = loggerMock(2)
            Result(CompletableFuture.completedFuture(1)).logOnFailure {
                logger.warn(it.toString())
            }
            verify(exactly = 0) {
                logger.warn(any())
            }
        }
    }
}