package com.tomaszezula.makker.adapter

import com.tomaszezula.makker.adapter.model.IndefiniteScheduling
import com.tomaszezula.makker.adapter.model.Scheduling
import com.tomaszezula.makker.adapter.model.Scheduling.Companion.MinInterval

fun Scheduling.validate(): Result<Scheduling> = when (this) {
    is IndefiniteScheduling -> {
        if (this.interval >= MinInterval) Result.success(this)
        else Result.failure(IllegalStateException("The scheduling interval must be at least ${MinInterval}s!"))
    }
}
