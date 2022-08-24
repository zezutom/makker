package com.tomaszezula.makker.client.jvm.java.model

import kotlinx.serialization.Serializable

@Serializable
data class Scenario(val id: Int, val teamId: Int, val folderId: Int?, val name: String)

