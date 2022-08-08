package com.tomaszezula.makker.common.model

import com.tomaszezula.makker.common.model.Scheduling.Companion.MinInterval

sealed interface Scheduling {
    fun toJson(): String

    companion object {
        const val MinInterval = 900
    }
}

data class IndefiniteScheduling(val interval: Int = MinInterval) : Scheduling {
    override fun toJson(): String =
        """
            {"type":"indefinitely","interval":$interval}            
        """.trim()
}

fun Scheduling.validate(): Result<Scheduling> = when (this) {
    is IndefiniteScheduling -> {
        if (this.interval >= MinInterval) Result.success(this)
        else Result.failure(IllegalStateException("The scheduling interval must be at least ${MinInterval}s!"))
    }
}