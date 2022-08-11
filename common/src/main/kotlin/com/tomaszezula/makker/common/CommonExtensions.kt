package com.tomaszezula.makker.common

fun <T> List<Result<T>>.toResult(): Result<List<T>> =
    if (this.any { it.isFailure }) {
        Result.failure(IllegalStateException("Operation failed for one or more fields."))
    } else {
        Result.success(this.map { it.getOrThrow() })
    }
