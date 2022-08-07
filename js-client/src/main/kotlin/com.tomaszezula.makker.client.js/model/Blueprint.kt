package com.tomaszezula.makker.client.js.model

import kotlinx.serialization.Serializable

@Serializable
data class Blueprint(val name: String, val modules: List<Module>, val json: String) {
    @Serializable
    data class Module(val id: Long, val name: String)
}
