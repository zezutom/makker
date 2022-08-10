package com.tomaszezula.makker.common

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.tomaszezula.makker.common.model.*
import com.tomaszezula.makker.common.model.exception.BadRequestException
import com.tomaszezula.makker.common.model.exception.NotFoundException
import com.tomaszezula.makker.common.model.exception.ServerErrorException
import com.tomaszezula.makker.common.model.exception.UnexpectedStatusCode
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.*

class DefaultMakeAdapter(
    private val config: MakeConfig,
    private val httpClient: HttpClient,
    private val json: Json
) : MakeAdapter {

    companion object {
        const val BlueprintKey = "blueprint"
        const val FlowKey = "flow"
        const val FolderIdKey = "folderId"
        const val IdKey = "id"
        const val ModuleKey = "module"
        const val NameKey = "name"
        const val ResponseKey = "response"
        const val SchedulingKey = "scheduling"
        const val ScenarioKey = "scenario"
        const val Separator = ""
        const val TeamIdKey = "teamId"
    }

    private val createScenarioUrl = "${config.baseUrl}/scenarios?confirmed=true"

    private val objectMapper = ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .registerModule(KotlinModule.Builder().build())

    override suspend fun createScenario(
        teamId: Scenario.TeamId,
        folderId: Scenario.FolderId,
        blueprintJson: Blueprint.Json,
        scheduling: Scheduling,
        token: AuthToken
    ): Result<Scenario> =
        scheduling.validate().map { schedule ->
            post(createScenarioUrl, token, buildJsonObject {
                put(BlueprintKey, blueprintJson.toJson())
                put(SchedulingKey, schedule.toJson())
                put(TeamIdKey, teamId.value)
                put(FolderIdKey, folderId.value)
            }) { it.toScenario() }
        }.getOrThrow()

    override suspend fun updateScenario(
        scenarioId: Scenario.Id,
        blueprint: Blueprint.Json,
        token: AuthToken
    ): Result<Scenario> =
        patch("${config.baseUrl}/scenarios/${scenarioId.value}?confirmed=true", token, buildJsonObject {
            put(BlueprintKey, blueprint.value)
        }) { it.toScenario() }

    override suspend fun getBlueprint(scenarioId: Scenario.Id, token: AuthToken): Result<Blueprint> =
        get("${config.baseUrl}/scenarios/${scenarioId.value}/blueprint", token) { responseJson ->
            jsonObject(responseJson, ResponseKey, BlueprintKey)?.let { blueprintJson ->
                blueprintJson[NameKey]?.jsonPrimitive?.content?.let { name ->
                    Blueprint(
                        name,
                        blueprintJson[FlowKey]?.jsonArray?.mapNotNull { it.toModule() } ?: emptyList(),
                        Blueprint.Json(blueprintJson.toString())
                    )
                }
            }
        }

    override suspend fun setModuleData(
        scenarioId: Scenario.Id,
        moduleId: Blueprint.Module.Id,
        fieldName: String,
        data: String,
        token: AuthToken
    ): Result<Boolean> {
        val updatedBlueprint = get("${config.baseUrl}/scenarios/${scenarioId.value}/blueprint", token) { responseJson ->
            val blueprint = objectMapper.readValue(responseJson.toString(), com.tomaszezula.makker.make.api.Blueprint::class.java)

            // Modify the blueprint. This is a hack!
            blueprint.response.blueprint.flow.find { it.id == moduleId.value.toInt() }?.let { module ->
                val node: ObjectNode = objectMapper.valueToTree(module.mapper.additionalProperties)
                val jsonData: JsonNode = objectMapper.valueToTree(data)
                val updatedNode: ObjectNode = node.set(fieldName, jsonData)
                val updatedProperties = objectMapper.readValue(updatedNode.toString(), Map::class.java)
                module.mapper.additionalProperties.clear()
                module.mapper.additionalProperties.putAll(updatedProperties.map { (it.key.toString() to it.value) })
            }
            blueprint
        }.getOrThrow()

        return patch("${config.baseUrl}/scenarios/${scenarioId.value}?confirmed=true", token, buildJsonObject {
            put(BlueprintKey, objectMapper.writeValueAsString(updatedBlueprint.response.blueprint))
        }) { true }
    }

    private fun jsonObject(jsonObject: JsonObject?, vararg path: String): JsonObject? {
        return if (path.isEmpty()) jsonObject
        else jsonObject(
            jsonObject?.let { it[path.first()]?.jsonObject },
            *path.drop(1).toTypedArray()
        )
    }

    private suspend fun <T> get(url: String, token: AuthToken, f: (JsonObject) -> T?): Result<T> =
        httpClient.get(url) {
            setHeaders(token)
        }.toResult(f)

    private suspend fun <T> post(url: String, token: AuthToken, body: JsonObject, f: (JsonObject) -> T?): Result<T> =
        httpClient.post(url) {
            setHeaders(token)
            setBody(body.toString())
        }.toResult(f)

    private suspend fun <T> patch(url: String, token: AuthToken, body: JsonObject, f: (JsonObject) -> T?): Result<T> =
        httpClient.patch(url) {
            setHeaders(token)
            setBody(body.toString())
        }.toResult(f)

    private fun HttpRequestBuilder.setHeaders(token: AuthToken) {
        headers {
            append(HttpHeaders.Authorization, "Token ${token.value}")
        }
        contentType(ContentType.Application.Json)
    }

    private suspend fun <T> HttpResponse.toResult(f: (JsonObject) -> T?): Result<T> =
        when (this.status) {
            HttpStatusCode.OK -> {
                f(json.parseToJsonElement(bodyAsText()).jsonObject)?.let {
                    Result.success(it)
                } ?: Result.failure(IllegalStateException("Failed to parse response body"))
            }
            HttpStatusCode.BadRequest -> Result.failure(BadRequestException(this.bodyAsText()))
            HttpStatusCode.NotFound -> Result.failure(NotFoundException())
            HttpStatusCode.InternalServerError -> Result.failure(ServerErrorException(this.bodyAsText()))
            else -> Result.failure(UnexpectedStatusCode(this.status))
        }

    private fun JsonElement.toModule(): Blueprint.Module? =
        this.jsonObject[IdKey]?.jsonPrimitive?.longOrNull?.let { moduleId ->
            this.jsonObject[ModuleKey]?.jsonPrimitive?.content?.let { name ->
                Blueprint.Module(Blueprint.Module.Id(moduleId), name)
            }
        }

    private fun JsonObject.toScenario(): Scenario? =
        jsonObject(this, ScenarioKey)?.let {
            it[IdKey]?.jsonPrimitive?.longOrNull?.let { scenarioId ->
                it[TeamIdKey]?.jsonPrimitive?.longOrNull?.let { teamId ->
                    it[NameKey]?.jsonPrimitive?.content?.let { name ->
                        Scenario(
                            Scenario.Id(scenarioId),
                            Scenario.TeamId(teamId),
                            it[FolderIdKey]?.jsonPrimitive?.longOrNull?.let { folderId -> Scenario.FolderId(folderId) },
                            name
                        )
                    }
                }
            }
        }

    private fun Blueprint.Json.toJson(): String =
        this.value.lineSequence().map { it.trim() }.joinToString(Separator)
}