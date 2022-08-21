package com.tomaszezula.makker.common

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException

fun <T> List<Result<T>>.toResult(): Result<List<T>> =
    if (this.any { it.isFailure }) {
        Result.failure(IllegalStateException("Operation failed for one or more fields."))
    } else {
        Result.success(this.mapNotNull { it.getOrNull() })
    }

// https://github.com/Kotlin/kotlinx.coroutines/issues/1814
inline fun <R> runSuspendCatching(block: () -> R): Result<R> {
    return try {
        Result.success(block())
    } catch (t: TimeoutCancellationException) {
        Result.failure(t)
    } catch (c: CancellationException) {
        throw c
    } catch (e: Throwable) {
        Result.failure(e)
    }
}

suspend fun <A, B> Result<A>.then(block: suspend (A) -> Result<B>): Result<B> =
    runSuspendCatching {
        this.map {
            block(it).getOrThrow()
        }.getOrThrow()
    }

fun <T> Result<T>.logOnSuccess(block: () -> Unit): Result<T> =
    runSuspendCatching {
        this.onSuccess { block() }.getOrThrow()
    }

fun <T> Result<T>.logOnFailure(block: () -> Unit): Result<T> =
    runSuspendCatching {
        this.onFailure { block() }.getOrThrow()
    }

