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
import com.tomaszezula.makker.make.api.Mapper
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.*
import java.util.*

class DefaultMakeAdapter(
    private val config: MakeConfig,
    private val httpClient: HttpClient,
    private val json: Json
) : MakeAdapter {

    companion object {
        const val BlueprintKey = "blueprint"
        const val FlowKey = "flow"
        const val RoutesKey = "routes"
        const val FolderIdKey = "folderId"
        const val IdKey = "id"
        const val ModuleKey = "module"
        const val MapperKey = "mapper"
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
        blueprint: Blueprint.Json,
        scheduling: Scheduling,
        context: CreateScenarioContext
    ): Result<Scenario> =
        scheduling.validate().map { schedule ->
            post(createScenarioUrl, context.authToken, buildJsonObject {
                put(BlueprintKey, blueprint.toJson())
                put(SchedulingKey, schedule.toJson())
                put(TeamIdKey, context.teamId.value)
                put(FolderIdKey, context.folderId.value)
            }) { it.toScenario() }
        }.getOrThrow()

    override suspend fun updateScenario(
        blueprint: Blueprint.Json,
        context: UpdateScenarioContext
    ): Result<Scenario> =
        patch(
            "${config.baseUrl}/scenarios/${context.scenarioId.value}?confirmed=true",
            context.authToken,
            buildJsonObject {
                put(BlueprintKey, blueprint.toJson())
            }) { it.toScenario() }

    override suspend fun getBlueprint(scenarioId: Scenario.Id, token: AuthToken): Result<Blueprint> =
        get("${config.baseUrl}/scenarios/${scenarioId.value}/blueprint", token) { responseJson ->
            jsonObject(responseJson, ResponseKey, BlueprintKey)?.let { blueprintJson ->
                blueprintJson[NameKey]?.jsonPrimitive?.content?.let { name ->
                    Blueprint(
                        name,
                        extractModules(blueprintJson),
                        Blueprint.Json(blueprintJson.toString())
                    )
                }
            }
        }

    override suspend fun setModuleData(
        key: String,
        value: String,
        context: SetModuleDataContext
    ): Result<UpdateResult> {
        val updatedBlueprint = updateBlueprint(context, value, key)

        return patch(
            "${config.baseUrl}/scenarios/${context.scenarioId.value}?confirmed=true",
            context.authToken,
            buildJsonObject {
                put(BlueprintKey, objectMapper.writeValueAsString(updatedBlueprint.response.blueprint))
            }) {
            UpdateResult.Success
        }
    }

    private suspend fun updateBlueprint(
        context: SetModuleDataContext,
        value: String,
        key: String
    ) = get("${config.baseUrl}/scenarios/${context.scenarioId.value}/blueprint", context.authToken) { responseJson ->
        val blueprint =
            objectMapper.readValue(responseJson.toString(), com.tomaszezula.makker.make.api.Blueprint::class.java)

        // Modify the blueprint. This is a hack!
        blueprint.response.blueprint.flow.find { it.id == context.moduleId.value }?.let { module ->
            if (module.mapper == null) {
                module.mapper = Mapper()
            }
            val node: ObjectNode = objectMapper.valueToTree(module.mapper.additionalProperties.orEmpty())
            val jsonData: JsonNode = objectMapper.valueToTree(value)
            val updatedNode: ObjectNode = node.set(key, jsonData)
            val updatedProperties = objectMapper.readValue(updatedNode.toString(), Map::class.java)
            module.mapper.additionalProperties.clear()
            module.mapper.additionalProperties.putAll(updatedProperties.map { (it.key.toString() to it.value) })
        } ?: run {
            blueprint.response.blueprint.flow.forEach { flow ->
                replaceModuleData(flow.additionalProperties, context.moduleId.value, key, value)
            }
        }
        blueprint
    }.getOrThrow()

    private fun extractModules(
        jsonObject: JsonObject,
        modules: List<Blueprint.Module> = emptyList()
    ): List<Blueprint.Module> {
        return jsonObject[FlowKey]?.jsonArray?.flatMap { flow ->
            flow.jsonObject[RoutesKey]?.jsonArray?.flatMap { route ->
                extractModules(route.jsonObject, modules)
            } ?: run {
                flow.toModule()?.let { module -> extractModules(flow.jsonObject, modules.plus(module)) } ?: modules
            }
        } ?: modules
    }

    @Suppress("UNCHECKED_CAST")
    private fun replaceModuleData(map: MutableMap<String, Any>, moduleId: Int, key: String, value: String) {
        map[RoutesKey]?.let { it as List<*> }?.forEach {
            replaceModuleData(it as MutableMap<String, Any>, moduleId, key, value)
        } ?: map[FlowKey]?.let { it as List<Map<String, Any>> }?.forEach {
            replaceModuleData(it as MutableMap<String, Any>, moduleId, key, value)
        } ?: map[IdKey]?.let { id ->
            if ((id as Int) == moduleId) {
                map.replace(MapperKey, mapOf(key to value))
            }
        }
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
        this.jsonObject[IdKey]?.jsonPrimitive?.intOrNull?.let { moduleId ->
            this.jsonObject[ModuleKey]?.jsonPrimitive?.content?.let { name ->
                Blueprint.Module(Blueprint.Module.Id(moduleId), name)
            }
        }

    private fun JsonObject.toScenario(): Scenario? =
        jsonObject(this, ScenarioKey)?.let {
            it[IdKey]?.jsonPrimitive?.intOrNull?.let { scenarioId ->
                it[TeamIdKey]?.jsonPrimitive?.intOrNull?.let { teamId ->
                    it[NameKey]?.jsonPrimitive?.content?.let { name ->
                        Scenario(
                            Scenario.Id(scenarioId),
                            Scenario.TeamId(teamId),
                            it[FolderIdKey]?.jsonPrimitive?.intOrNull?.let { folderId -> Scenario.FolderId(folderId) },
                            name
                        )
                    }
                }
            }
        }

    private fun Blueprint.Json.toJson(): String =
        (if (this.encoded) String(Base64.getDecoder().decode(this.value)) else this.value)
            .lineSequence().map { it.trim() }.joinToString(Separator)
}