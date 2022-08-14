package com.tomaszezula.makker.common.model

import kotlinx.serialization.Serializable

@Serializable
data class Scenario(val id: Id, val teamId: TeamId, val folderId: FolderId?, val name: String) {
    @Serializable
    @JvmInline
    value class Id(val value: Int)

    @Serializable
    @JvmInline
    value class TeamId(val value: Int)

    @Serializable
    @JvmInline
    value class FolderId(val value: Int)
}
