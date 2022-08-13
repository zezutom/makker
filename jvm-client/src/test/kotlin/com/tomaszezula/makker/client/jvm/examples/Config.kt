package com.tomaszezula.makker.client.jvm.examples

import com.tomaszezula.makker.client.jvm.DefaultMakeClient
import com.tomaszezula.makker.client.jvm.MakeClient
import com.tomaszezula.makker.common.DefaultMakeAdapter
import com.tomaszezula.makker.common.MakeConfig
import com.tomaszezula.makker.common.model.AuthToken
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import kotlinx.serialization.json.Json
import java.net.URL

object Config {
    val makeClient: MakeClient = DefaultMakeClient(
        DefaultMakeAdapter(
            MakeConfig.eu(),
            HttpClient(CIO),
            Json { ignoreUnknownKeys = true }
        ), AuthToken("REPLACE-WITH-YOUR-API-KEY"))

    val blueprint = getResource("blueprint.json").readText()
    val updatedBlueprint = getResource("blueprint-updated.json").readText()

    fun getResource(name: String): URL =
        Config::class.java.classLoader.getResource(name)!!
}
