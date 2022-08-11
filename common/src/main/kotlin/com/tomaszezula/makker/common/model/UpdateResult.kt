package com.tomaszezula.makker.common.model

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class UpdateResult(val result: Boolean)
