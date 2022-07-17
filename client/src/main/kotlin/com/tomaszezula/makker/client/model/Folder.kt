package com.tomaszezula.makker.client.model

data class Folder(val id: Id, val scenarios: List<Scenario>) {
    @JvmInline
    value class Id(val value: Int)
}
