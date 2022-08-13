package com.tomaszezula.makker.client.jvm.examples

import com.tomaszezula.makker.client.jvm.MakeClient
import java.net.URL

object Config {
    val makeClient = MakeClient.eu("REPLACE-WITH-YOUR-API-KEY")

    val blueprint = getResource("blueprint.json").readText()
    val updatedBlueprint = getResource("blueprint-updated.json").readText()

    fun getResource(name: String): URL =
        Config::class.java.classLoader.getResource(name)!!
}
