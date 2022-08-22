package com.tomaszezula.makker.client.jvm.examples

import com.tomaszezula.makker.client.jvm.MakeClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URL

object Config {
    val logger: Logger = LoggerFactory.getLogger("Main")
    val makeClient = MakeClient.eu("REPLACE-WITH-YOUR-API-KEY")
    val blueprint = getResource("blueprint.json").readText()
    val updatedBlueprint = getResource("blueprint-updated.json").readText()

    fun getResource(name: String): URL =
        Config::class.java.classLoader.getResource(name)!!
}
