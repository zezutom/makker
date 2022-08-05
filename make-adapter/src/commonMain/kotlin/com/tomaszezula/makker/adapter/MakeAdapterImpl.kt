package com.tomaszezula.makker.adapter

import com.tomaszezula.makker.adapter.model.AuthToken
import com.tomaszezula.makker.adapter.model.Blueprint
import com.tomaszezula.makker.adapter.model.Scenario
import com.tomaszezula.makker.adapter.model.Scheduling
import com.tomaszezula.makker.adapter.model.exception.BadRequestException
import com.tomaszezula.makker.adapter.model.exception.NotFoundException
import com.tomaszezula.makker.adapter.model.exception.ServerErrorException
import com.tomaszezula.makker.adapter.model.exception.UnexpectedStatusCode
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.*

class MakeAdapterImpl(
    private val token: AuthToken,
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
        const val UpdatedKey = "updated"
    }

    private val createScenarioUrl = "${config.baseUrl}/scenarios?confirmed=true"

    override suspend fun createScenario(
        teamId: Scenario.TeamId,
        folderId: Scenario.FolderId,
        blueprint: Blueprint.Json,
        scheduling: Scheduling
    ): Result<Scenario> =
        scheduling.validate().map { schedule ->
            post(createScenarioUrl, buildJsonObject {
                put(BlueprintKey, blueprint.value.lineSequence().map { it.trim() }.joinToString(Separator))
                put(SchedulingKey, schedule.toJson())
                put(TeamIdKey, teamId.value)
                put(FolderIdKey, folderId.value)
            }) { it.toScenario() }
        }.getOrThrow()

    override suspend fun updateScenario(scenarioId: Scenario.Id, blueprint: Blueprint.Json): Result<Scenario> =
        patch("${config.baseUrl}/scenarios/${scenarioId.value}?confirmed=true", buildJsonObject {
            put(BlueprintKey, blueprint.value)
        }) { it.toScenario() }

    override suspend fun getBlueprint(scenarioId: Scenario.Id): Result<Blueprint> =
        get("${config.baseUrl}/scenarios/$scenarioId/blueprint") { responseJson ->
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
        data: String
    ): Result<Boolean> =
        put("${config.baseUrl}/scenarios/$scenarioId/data", buildJsonObject {
            put(moduleId.value.toString(), json.encodeToJsonElement(buildJsonObject {
                put(fieldName, data)
            }))
        }) { responseJson ->
            responseJson[UpdatedKey]?.jsonPrimitive?.boolean ?: false
        }

    private fun jsonObject(jsonObject: JsonObject?, vararg path: String): JsonObject? {
        return if (path.isEmpty()) jsonObject
        else jsonObject(
            jsonObject?.let { it[path.first()]?.jsonObject },
            *path.drop(1).toTypedArray()
        )
    }

    private suspend fun <T> get(url: String, f: (JsonObject) -> T?): Result<T> =
        httpClient.get(url) {
            setHeaders()
        }.toResult(f)

    private suspend fun <T> post(url: String, body: JsonObject, f: (JsonObject) -> T?): Result<T> =
        httpClient.post(url) {
            setHeaders()
            setBody(body.toString())
        }.toResult(f)

    private suspend fun <T> put(url: String, body: JsonObject, f: (JsonObject) -> T?): Result<T> =
        httpClient.put(url) {
            setHeaders()
            setBody(body.toString())
        }.toResult(f)

    private suspend fun <T> patch(url: String, body: JsonObject, f: (JsonObject) -> T?): Result<T> =
        httpClient.patch(url) {
            setHeaders()
            setBody(body.toString())
        }.toResult(f)

    private fun HttpRequestBuilder.setHeaders() {
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
}