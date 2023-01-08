package com.langnerd.makker.client.model

data class App(val id: Id, val name: String) {
    @JvmInline
    value class Id(val value: String)
}
