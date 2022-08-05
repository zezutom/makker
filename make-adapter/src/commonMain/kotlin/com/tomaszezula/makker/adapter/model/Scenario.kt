package com.tomaszezula.makker.adapter.model

import kotlinx.serialization.Serializable

@Serializable
data class Scenario(val id: Id, val teamId: TeamId, val folderId: FolderId, val name: String) {
    @Serializable
    @JvmInline
    value class Id(val value: Long)

    @Serializable
    @JvmInline
    value class TeamId(val value: Long)

    @Serializable
    @JvmInline
    value class FolderId(val value: Long)
}
