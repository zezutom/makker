package com.tomaszezula.makker.client.model

data class Module(val id: Id, val name: String, val model: Map<String, Any>) {
    @JvmInline
    value class Id(val value: Int)
}
