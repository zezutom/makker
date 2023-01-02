package com.langnerd.makker.utils

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.langnerd.makker.model.MakeApp

class MakeAppGenerator(private val mapper: ObjectMapper) {

    private val schemaTypeMap = mapOf(
        "account:google-restricted" to "integer",
        "text" to "string",
        "boolean" to "boolean",
        "uinteger" to "integer",
    )

    fun generate(json: String): Result<MakeApp> {
        val jsonNode = mapper.readTree(json)
        val name = jsonNode.getName()
        val actions = jsonNode.getField("actions")?.let { actions(it) } ?: emptyList()
        val feeders = jsonNode.getField("feeders")?.let { feeders(name, it) } ?: emptyList()
        val triggers = jsonNode.getField("triggers")?.let { triggers(name, it) } ?: emptyList()
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
            jsonNode.asIterable()
                .map { module(parent, it, ::feederProperties, MakeApp::Feeder) }
                .map { it.getOrThrow() }
        } else emptyList()
    }

    private fun triggers(parent: String, jsonNode: JsonNode): List<MakeApp.Trigger> {
        return if (jsonNode.isArray) {
            jsonNode.asIterable()
                .map { module(parent, it, ::triggerProperties, MakeApp::Trigger) }
                .map { it.getOrThrow() }
        } else emptyList()
    }

    private fun <T> module(
        parent: String,
        jsonNode: JsonNode,
        propertyExtractor: (JsonNode) -> JsonNode,
        successHandler: (String, String) -> T
    ): Result<T> {
        val schemaNode = mapper.createObjectNode()
        val name = jsonNode.getName()
        schemaNode.put("\$schema", "https://json-schema.org/draft/2020-12/schema")
        schemaNode.put("\$id", "https://langnerd.com/schemas/$parent/$name.json")
        schemaNode.put("type", "object")
        schemaNode.putIfAbsent("properties", propertyExtractor(jsonNode))
        schemaNode.put("additionalProperties", false)
        val schema = schemaNode.toPrettyString()
        return Result.success(successHandler(name, schema))
    }

    private fun JsonNode.toIterable(): List<JsonNode> =
        this.asIterable().filter { it.has("name") && it.has("type") }

    private fun JsonNode.addValueNode(parentNode: ObjectNode, valueNodeBuilder: ((ObjectNode) -> Unit)? = null) {
        val valueNode = mapper.createObjectNode()
        val type = this.getField("type")!!.asText()

        if (type == "select") {
            this.addEnum(valueNode)
        } else valueNode.put("type", schemaTypeMap[type] ?: "string")

        valueNodeBuilder?.let { it(valueNode) }
        val name = this.getField("name")!!.asText()
        parentNode.putIfAbsent(name, valueNode)
    }

    private fun JsonNode.addEnum(parentNode: ObjectNode) {
        if (this.has("options")) {
            val options = this.getField("options")!!
            if (options.isArray) {
                val arrayNode = mapper.createArrayNode()
                options.asIterable().filter { it.has("value") }.forEach { option ->
                    arrayNode.add(option.getField("value")!!.asText())
                }
                parentNode.putIfAbsent("enum", arrayNode)
                if (this.has("default")) {
                    val defaultValue = this.getField("default")!!.asText().lowercase()
                    arrayNode.asIterable()
                        .firstOrNull { it.asText().lowercase() == defaultValue }?.let {
                            parentNode.putIfAbsent("default", it)
                        }
                }
            }
        }
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
                expectNode.toIterable().forEach { expectPropertyNode ->
                    expectPropertyNode.addValueNode(propertyNode) { valueNode ->
                        if (expectPropertyNode.has("pattern")) {
                            valueNode.put("default", expectPropertyNode.get("pattern").asText())
                        }
                    }
                }
            }
        }
        return propertyNode
    }

    private fun triggerProperties(jsonNode: JsonNode): JsonNode {
        val propertyNode = mapper.createObjectNode()
        if (jsonNode.has("parameters")) {
            val parametersNode = jsonNode.getField("parameters")!!
            if (parametersNode.isArray) {
                parametersNode.toIterable().forEach { parameterNode ->
                    parameterNode.addValueNode(propertyNode) {
                        if (parameterNode.has("options")) {
                            optionsProperties(propertyNode, parameterNode.getField("options")!!)
                        }
                        if (parameterNode.has("default")) {
                            it.putIfAbsent("default", parameterNode.get("default"))
                        }
                    }
                }
            }
        }
        return propertyNode
    }

    private fun optionsProperties(parentNode: ObjectNode, jsonNode: JsonNode) {
        if (jsonNode.isObject) {
            optionProperties(parentNode, jsonNode)
        } else if (jsonNode.isArray) {
            jsonNode.asIterable().forEach { optionProperties(parentNode, it) }
        }
    }

    private fun optionProperties(parentNode: ObjectNode, jsonNode: JsonNode) {
        if (jsonNode.has("nested")) {
            val nestedNode = jsonNode.getField("nested")!!
            if (nestedNode.isArray) {
                nestedNode.toIterable().forEach {
                    it.addValueNode(parentNode) { valueNode ->
                        if (it.has("default")) {
                            valueNode.putIfAbsent("default", it.get("default"))
                        }
                    }
                }
            }
        }
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