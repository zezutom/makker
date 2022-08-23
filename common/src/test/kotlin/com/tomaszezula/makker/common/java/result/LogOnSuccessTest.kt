package com.tomaszezula.makker.common.java.result

import com.tomaszezula.makker.common.java.Result
import io.kotest.core.spec.style.StringSpec
import io.mockk.verify
import java.util.concurrent.CompletableFuture

class LogOnSuccessTest : StringSpec() {
    init {
        "It executes the provided callback" {
            val x = 1
            val logger = loggerMock(x)
            Result(CompletableFuture.completedFuture(1)).logOnSuccess {
                logger.info(it.toString())
            }
            verify(exactly = 1) {
                logger.info(x.toString())
            }
        }
        "It does not execute the provided callback on failure" {
            val logger = loggerMock(2)
            Result(CompletableFuture.failedFuture<Int>(NumberFormatException())).logOnSuccess {
                logger.info(it.toString())
            }
            verify(exactly = 0) {
                logger.info(any())
            }
        }
    }
}