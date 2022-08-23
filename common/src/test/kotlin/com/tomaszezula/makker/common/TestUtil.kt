package com.tomaszezula.makker.common.java.result

import io.mockk.every
import io.mockk.mockk
import org.slf4j.Logger

fun <T> loggerMock(value: T): Logger {
    val logger = mockk<Logger>()
    every {
        logger.debug(value.toString())
        logger.info(value.toString())
        logger.warn(value.toString())
        logger.error(value.toString())
    } returns Unit
    return logger
}