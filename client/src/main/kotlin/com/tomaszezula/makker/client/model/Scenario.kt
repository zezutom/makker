package com.tomaszezula.makker.client.model

data class Scenario(val id: Id, val name: String) {
    @JvmInline
    value class Id(val value: Int)
}
