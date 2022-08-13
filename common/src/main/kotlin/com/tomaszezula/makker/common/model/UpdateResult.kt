package com.tomaszezula.makker.common.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdateResult(val result: Boolean) {
    companion object {
        val Success = UpdateResult(true)
        val Failure = UpdateResult(false)
    }
}
