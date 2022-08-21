package com.tomaszezula.makker.common.extensions

import com.tomaszezula.makker.common.logOnSuccess
import io.kotest.core.spec.style.StringSpec
import io.mockk.mockk
import io.mockk.verify
import org.slf4j.Logger

class LogOnSuccessTest : StringSpec() {
    init {
        "executes on success" {
            test {
                Result.success("hello world")
            }
        }
        "does not execute on failure" {
            test(shouldLog = false) {
                Result.failure<Int>(NumberFormatException())
            }
        }
    }
    private fun<T> test(message: String = "test message", shouldLog: Boolean = true, block: () -> Result<T>) {
        val logger = mockk<Logger>()
        block().logOnSuccess { logger.info(message) }

        verify(exactly = if (shouldLog) 1 else 0) {
            logger.info(message)
        }
    }
}