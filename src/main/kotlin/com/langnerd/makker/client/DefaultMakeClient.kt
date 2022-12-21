package com.langnerd.makker.client

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.jsonschema.JsonSchema
import com.fasterxml.jackson.databind.node.ObjectNode
import com.langnerd.makker.model.Module

class DefaultMakeClient(private val mapper: ObjectMapper) : MakeClient {

    /**
     * Module:
     * - id: arbitrary int (sequence?)
     * - module: App detail: name + ":" + triggers["name"] => ex.: "google-email:TriggerNewEmail"
     * - version: extract major version from "version" => ex.: "2.0.12" => 2
     * - parameters: triggers["name]["parameters"]: flattened: parameter + parameter["options"]["nested"]
     * - metadata: this is just a visual position on the canvas (x, y)
     *
     * Organize it as a flow left to right. Modules are automatically linked with each other.
     *
     * Each module should have a generated data class. The generator works as follows:
     * 1. Input: raw app description, e.g. all triggers and all the config
     * 2. For each trigger create a simple JSON schema: parameters + nested options => flattened. Also consider enums.
     * 3. Save each JSON schema to a separate file (<<Trigger_Name>>.json).
     * 4. Use a library to generate data classes from the JSON schema files.
     * 5. Use the logic below to plug the generated data classes into the scenario "flow".
     *
     */
    override fun getModules(application: String): List<Module> {
        return this::class.java.getResource("json/$application.json")?.readText(Charsets.UTF_8)?.let { json ->
            val jsonNode = mapper.readTree(json)
            listOf(
                jsonNode.getField("actions")?.let { processActions(it) } ?: emptyList(),
                jsonNode.getField("feeders")?.let { processFeeders(it) } ?: emptyList(),
                jsonNode.getField("triggers")?.let { processTriggers(it) } ?: emptyList(),
            ).flatten()
        } ?: emptyList()
    }

    private fun JsonNode.getField(fieldName: String): JsonNode? {
        return if (this.has(fieldName)) this[fieldName] else null
    }

    private fun processActions(rootNode: JsonNode): List<Module> {
        return if (rootNode.isArray) {
            // For each action in the array, create a schema and save it into module named after the action
            val schemaNode = mapper.createObjectNode()
            schemaNode.put("type", "object")
            schemaNode.put("properties", "object")
            TODO()
        } else emptyList<Module>()
    }

    private fun processFeeders(rootNode: JsonNode): List<Module> {
        return if (rootNode.isArray) {
            // For each feeder in the array, create a schema and save it into module named after the feeder
            val schemaNode = mapper.createObjectNode()
            schemaNode.put("type", "object")
            schemaNode.put("properties", "object")
            TODO()
        } else emptyList<Module>()
    }

    private fun processTriggers(rootNode: JsonNode): List<Module> {
        return if (rootNode.isArray) {
            // For each trigger in the array, create a schema and save it into module named after the trigger
            val schemaNode = mapper.createObjectNode()
            schemaNode.put("type", "object")
            schemaNode.put("properties", "object")
            TODO()
        } else emptyList<Module>()
    }
}