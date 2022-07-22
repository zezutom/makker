package com.tomaszezula.makker.client

import com.tomaszezula.makke.api.Flow
import com.tomaszezula.makke.api.Mapper
import com.tomaszezula.makker.client.model.Module

fun Flow.toModule(): Module? =
    if (this.id != null && this.module != null) {
        Module(
            Module.Id(this.id),
            this.module.toString(),
            this.mapper?.toModel().orEmpty()
        )
    } else null

private fun Mapper.toModel(): Map<String, Any> =
    this.additionalProperties?.mapNotNull {
        (it.key to it.value)
    }?.toMap().orEmpty()