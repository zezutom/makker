package com.tomaszezula.makker.adapter.model

import com.tomaszezula.makker.adapter.model.Scheduling.Companion.MinInterval

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
