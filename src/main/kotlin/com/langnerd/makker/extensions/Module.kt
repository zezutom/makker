package com.langnerd.makker.extensions

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.langnerd.makker.model.Module

// Map of supported modules and their versions. TODO this should be generated.
private val supportedModules = mapOf(
    "google-email" to 2
)
private val excludedProperties = listOf("input")

fun Module.move(x: Int, y: Int): Module =
    this.copy(metadata = Module.ModuleMetadata(Module.ModuleMetadata.Designer(x, y)))

// This relies on a package structure of the generated API classes: `com.tomaszezula.makker.make.api.<module_name>.{actions|triggers|feeders}`
fun Any.toModule(id: Long): Module? {
    val canonicalName = this.javaClass.canonicalName
    val chunks = canonicalName.split(".")
    return if (chunks.size < 3) null else {
        val moduleName = chunks[chunks.size - 3].replace("_", "-")
        supportedModules[moduleName]?.let { version ->
            val mapper = ObjectMapper().registerKotlinModule().setSerializationInclusion(JsonInclude.Include.NON_NULL)
            val props: Map<String, Any> = mapper.convertValue(this, Map::class.java) as (Map<String, Any>)
            Module(
                id = id,
                module = "$moduleName:${this.javaClass.simpleName}",
                version = version,
                parameters = props.filter { !excludedProperties.contains(it.key) },
                metadata = Module.ModuleMetadata.Default
            )
        }
    }
}
