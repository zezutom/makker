package com.tomaszezula.makker.client.model

data class Team(val id: Id, val folders: List<Folder>) {
    @JvmInline
    value class Id(val value: Int)
}
