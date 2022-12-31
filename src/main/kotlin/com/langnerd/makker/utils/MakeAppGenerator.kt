package com.langnerd.makker.utils

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.langnerd.makker.model.MakeApp

class MakeAppGenerator(private val mapper: ObjectMapper) {

    fun generate(json: String): Result<MakeApp> {
        val jsonNode = mapper.readTree(json)
        val name = jsonNode.getName()
        val actions = jsonNode.getField("actions")?.let { actions(it) } ?: emptyList()
        val feeders = jsonNode.getField("feeders")?.let { feeders(name, it) } ?: emptyList()
        val triggers = jsonNode.getField("triggers")?.let { triggers(it) } ?: emptyList()
        // TODO
        return Result.success(MakeApp(name, actions, feeders, triggers))
    }

    private fun JsonNode.getField(fieldName: String): JsonNode? {
        return if (this.has(fieldName)) this[fieldName] else null
    }

    private fun JsonNode.getName(): String =
        this.getField("name")?.let { PropertyNamingStrategies.KebabCaseStrategy().translate(it.asText()) }
            ?: "undefined"

    private fun actions(jsonNode: JsonNode): List<MakeApp.Action> {
        return emptyList()
    }

    private fun feeders(parent: String, jsonNode: JsonNode): List<MakeApp.Feeder> {
        return if (jsonNode.isArray) {
            jsonNode.asIterable().map { feeder(parent, it) }.map { it.getOrThrow() }
        } else emptyList()
    }

    private fun feeder(parent: String, jsonNode: JsonNode): Result<MakeApp.Feeder> {
        val schemaNode = mapper.createObjectNode()
        val name = jsonNode.getName()
        schemaNode.put("\$schema", "https://json-schema.org/draft/2020-12/schema")
        schemaNode.put("\$id", "https://langnerd.com/schemas/$parent/$name.json")
        schemaNode.put("type", "object")
        schemaNode.putIfAbsent("properties", feederProperties(jsonNode))
        schemaNode.put("additionalProperties", false)
        val schema = schemaNode.toPrettyString()
        return Result.success(MakeApp.Feeder(name, schema))
    }

    private fun triggers(jsonNode: JsonNode): List<MakeApp.Trigger> {
        return emptyList()
    }

    private fun feederProperties(jsonNode: JsonNode): JsonNode {
        val propertyNode = mapper.createObjectNode()
        if (jsonNode.has("requires")) {
            val module = jsonNode.getField("requires")!!.asText()
            val chunks = module.split(":")
            if (chunks.size == 2) {
                val refNode = mapper.createObjectNode()
                val name = PropertyNamingStrategies.KebabCaseStrategy().translate(chunks[1])
                refNode.put("\$ref", "../triggers/$name.json")
                propertyNode.putIfAbsent("input", refNode)
            }
        }
        if (jsonNode.has("expect")) {
            val expectNode = jsonNode.getField("expect")!!
            if (expectNode.isArray) {
                expectNode.asIterable().filter { it.has("name") && it.has("type") }.forEach { expectPropertyNode ->
                    val name = expectPropertyNode.getField("name")!!.asText()
                    val valueNode = mapper.createObjectNode()
                    valueNode.put("type", "string")
                    if (expectPropertyNode.has("pattern")) {
                        valueNode.put("default", expectPropertyNode.get("pattern").asText())
                    }
                    propertyNode.putIfAbsent(name, valueNode)
                }
            }
        }
        return propertyNode
    }
}

fun main() {
    val mapper = ObjectMapper().registerKotlinModule().setSerializationInclusion(JsonInclude.Include.NON_NULL)
    val generator = MakeAppGenerator(mapper)
    val application = "google-email"
    MakeAppGenerator::class.java.getResource("/source/$application.json")?.readText(Charsets.UTF_8)?.let { json ->
        val makeApp = generator.generate(json)
        println(makeApp)
    }
}