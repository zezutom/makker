package com.tomaszezula.makker.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.future
import java.util.concurrent.CompletableFuture

fun <T> asCompletableFuture(
    scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
    f: suspend () -> Result<T>
): CompletableFuture<T> =
    scope.future {
        f().getOrThrow()
    }
