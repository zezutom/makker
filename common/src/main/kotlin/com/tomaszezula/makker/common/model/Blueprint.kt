package com.tomaszezula.makker.common.model

import kotlinx.serialization.Serializable

@Serializable
data class Blueprint(val name: String, val scenarioId: Scenario.Id, val modules: List<Module>, val json: Json) {
    @Serializable
    data class Module(val id: Id, val name: String) {
        @Serializable
        @JvmInline
        value class Id(val value: Int)
    }
    @Serializable
    data class Json(val value: String, val encoded: Boolean = false)
}
