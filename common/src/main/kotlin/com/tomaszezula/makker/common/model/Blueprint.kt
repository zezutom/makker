package com.tomaszezula.makker.common.model

import kotlinx.serialization.Serializable

@Serializable
data class Blueprint(val name: String, val modules: List<Module>, val json: Json) {
    @Serializable
    data class Module(val id: Id, val name: String) {
        @Serializable
        @JvmInline
        value class Id(val value: Long)
    }
    @Serializable
    @JvmInline
    value class Json(val value: String)
}
