package com.langnerd.makker.utils

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.langnerd.makker.model.MakeApp
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.text.Charsets.UTF_8

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
        val actions = jsonNode.getField("actions")?.let { actions(name, it) } ?: emptyList()
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

    private fun actions(parent: String, jsonNode: JsonNode): List<MakeApp.Action> {
        return if (jsonNode.isArray) {
            jsonNode.asIterable()
                .map { module(parent, it, ::actionProperties, MakeApp::Action) }
                .map { it.getOrThrow() }
        } else emptyList()
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

    private fun ObjectNode.addValueNode(jsonNode: JsonNode, valueNodeBuilder: ((ObjectNode) -> Unit)? = null) {
        val valueNode = mapper.createObjectNode()
        val type = jsonNode.getField("type")!!.asText()

        if (type == "select") {
            valueNode.addEnum(jsonNode)
        } else valueNode.put("type", schemaTypeMap[type] ?: "string")

        if (jsonNode.has("pattern")) {
            valueNode.put("default", jsonNode.get("pattern").asText())
        }

        if (jsonNode.has("default")) {
            valueNode.putIfAbsent("default", jsonNode.get("default"))
        }

        if (jsonNode.has("options")) {
            optionsProperties(this, jsonNode.getField("options")!!)
        }

        valueNodeBuilder?.let { it(valueNode) }
        val name = jsonNode.getField("name")!!.asText()
        this.putIfAbsent(name, valueNode)
    }

    private fun ObjectNode.addEnum(jsonNode: JsonNode) {
        if (jsonNode.has("options")) {
            val options = jsonNode.getField("options")!!
            if (options.isArray) {
                val arrayNode = mapper.createArrayNode()
                options.asIterable().filter { it.has("value") }.forEach { option ->
                    arrayNode.add(option.getField("value")!!.asText())
                }
                this.putIfAbsent("enum", arrayNode)
                if (jsonNode.has("default")) {
                    val defaultValue = jsonNode.getField("default")!!.asText().lowercase()
                    arrayNode.asIterable()
                        .firstOrNull { it.asText().lowercase() == defaultValue }?.let {
                            this.putIfAbsent("default", it)
                        }
                }
            }
        }
    }

    private fun ObjectNode.addArrayNode(jsonNode: JsonNode, fieldName: String): ObjectNode {
        if (jsonNode.has(fieldName)) {
            val arrayNode = jsonNode.getField(fieldName)!!
            if (arrayNode.isArray) {
                arrayNode.toIterable().forEach { this.addValueNode(it) }
            }
        }
        return this
    }

    private fun actionProperties(jsonNode: JsonNode): JsonNode {
        return mapper.createObjectNode()
            .addArrayNode(jsonNode, "parameters")
            .addArrayNode(jsonNode, "expect")
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
        return propertyNode.addArrayNode(jsonNode, "expect")
    }

    private fun triggerProperties(jsonNode: JsonNode): JsonNode {
        return mapper.createObjectNode()
            .addArrayNode(jsonNode, "parameters")
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
                    parentNode.addValueNode(it)
                }
            }
        }
    }
}

private fun resourceFolder(name: String): Result<File> {
    val file = Path.of("src", "main", "resources", name).toFile()
    return if (file.isDirectory) {
        Result.success(file)
    } else {
        Result.failure(IllegalArgumentException("$name is not a directory"))
    }
}

private fun loadModuleDefinitions(folder: String, regex: String? = null): List<String> {
    return resourceFolder(folder).getOrThrow()
        .listFiles()?.let { files ->
            val filteredFiles = regex?.let {
                val r = Regex.fromLiteral(it)
                files.filter { file -> file.name.matches(r) }
            } ?: files.toList()
            filteredFiles.map { it.readText(UTF_8) }
        } ?: emptyList()
}

private fun saveMakeApp(makeApp: MakeApp) {
    val folder = resourceFolder("json").getOrThrow()
    val appFolder = Files.createDirectories(folder.toPath().resolve(makeApp.name))
    makeApp.actions.forEach { saveResource(appFolder, "actions", it.name, it.schema) }
    makeApp.feeders.forEach { saveResource(appFolder, "feeders", it.name, it.schema) }
    makeApp.triggers.forEach { saveResource(appFolder, "triggers", it.name, it.schema) }
}

private fun saveResource(rootFolder: Path, folderName: String, filename: String, fileContent: String) {
    val targetPath = Files.createDirectories(rootFolder.resolve(folderName))
    targetPath.resolve("$filename.json").toFile().writeText(fileContent)
}

fun main() {
    val mapper = ObjectMapper().registerKotlinModule().setSerializationInclusion(JsonInclude.Include.NON_NULL)
    val generator = MakeAppGenerator(mapper)
    loadModuleDefinitions("source", "google-email.json").forEach { json ->
        saveMakeApp(generator.generate(json).getOrThrow())
    }
}