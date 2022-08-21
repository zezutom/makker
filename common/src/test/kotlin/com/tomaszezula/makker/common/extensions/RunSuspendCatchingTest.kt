package com.tomaszezula.makker.common.extensions

import com.tomaszezula.makker.common.runSuspendCatching
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CancellationException

class RunSuspendCatchingTest : StringSpec() {
    init {
        "returns success if there are no exceptions" {
            runSuspendCatching { listOf(1, 2, 3) } shouldBe Result.success(listOf(1, 2, 3))
        }
        "returns failure if there is a common exception" {
            val ex = IllegalStateException()
            runSuspendCatching { throw ex } shouldBe Result.failure(ex)
        }
        "throws CancellationException" {
            shouldThrow<CancellationException> {
                runSuspendCatching { throw CancellationException() }
            }
        }
    }
}