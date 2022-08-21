package com.tomaszezula.makker.common

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CommonExtensionsTest {

    @Test
    fun toResultSuccess() {
        assertEquals(
            Result.success(listOf(1, 2, 3)),
            listOf(
                Result.success(1),
                Result.success(2),
                Result.success(3)
            ).toResult()
        )
    }

    @Test
    fun toResultFailure() {
        assertTrue(
            listOf(
                Result.success(1),
                Result.success(2),
                Result.failure(NumberFormatException())
            ).toResult().isFailure
        )
    }

}