package com.tomaszezula.makker.common.extensions

import com.tomaszezula.makker.common.logOnFailure
import io.kotest.core.spec.style.StringSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.slf4j.Logger

class LogOnFailureTest : StringSpec() {
    init {
        "executes on failure" {
            test {
                Result.failure<Int>(NumberFormatException())
            }
        }
        "does not execute on success" {
            test(shouldLog = false) {
                Result.success("hello world")
            }
        }
    }
    private fun<T> test(shouldLog: Boolean = true, block: () -> Result<T>) {
        val logger = mockk<Logger>()
        every {
            logger.info(any())
        } returns Unit

        block().logOnFailure {
            val message = it.message.orEmpty()
            logger.info(message)

            verify(exactly = if (shouldLog) 1 else 0) {
                logger.info(message)
            }
        }
    }
}