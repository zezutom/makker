package com.langnerd.makker

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.langnerd.makker.client.DefaultMakeClient

fun main() {
    val mapper = ObjectMapper().registerKotlinModule().setSerializationInclusion(JsonInclude.Include.NON_NULL)
    val makeClient = DefaultMakeClient(mapper)
    val modules = makeClient.getModules("google-email")
    println(modules)
}