package com.tomaszezula.makker.client.model

data class Scenario(val id: Id, val blueprint: Blueprint, val modules: List<Module>) {
    @JvmInline
    value class Id(val value: Int)

    data class Module(val id: Id, val name: String, val model: Map<String, Any>) {
        @JvmInline
        value class Id(val value: Int)
    }
    @JvmInline
    value class Blueprint(val json: String)
}
