package com.tomaszezula.makker.client

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.tomaszezula.makke.api.Scenario__1
import com.tomaszezula.makker.client.config.MakerClientConfig
import com.tomaszezula.makker.client.model.*
import com.tomaszezula.makker.client.model.exception.BadRequestException
import com.tomaszezula.makker.client.model.exception.NotFoundException
import com.tomaszezula.makker.client.model.exception.ServerErrorException
import com.tomaszezula.makker.client.model.exception.UnexpectedServerResponseException
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class DefaultMakeClient(
    private val client: HttpClient,
    private val mapper: ObjectMapper,
    private val config: MakerClientConfig
) : MakeClient {

    // TODO logging
    companion object {
        object CreateScenario {
            object Params {
                const val Blueprint = "blueprint"
                const val Scheduling = "scheduling"
                const val FolderId = "folderId"
                const val TeamId = "teamId"
            }
        }

        object SetModuleData {
            object Params {
                const val Blueprint = "blueprint"
                const val Content = "content"
            }
        }
    }

    private val createScenarioUrl = "${config.baseUrl}/scenarios?confirmed=true"

    private val authToken = "Token ${config.token.value}"

    override suspend fun createScenario(
        teamId: Team.Id,
        folderId: Folder.Id,
        blueprintJson: BlueprintJson,
        scheduling: Scheduling
    ): Result<Scenario> =
        post(createScenarioUrl, buildJsonObject {
            put(CreateScenario.Params.Blueprint, blueprintJson.value.lineSequence().map { it.trim() }.joinToString(""))
            put(CreateScenario.Params.Scheduling, scheduling.toJson())
            put(CreateScenario.Params.TeamId, teamId.value)
            put(CreateScenario.Params.FolderId, folderId.value)
        }) { response ->
            val api = mapper.readValue(response.bodyAsText(), Scenario__1::class.java)
            Scenario(
                Scenario.Id(api.scenario.id),
                api.scenario.name
            )
        }

    override suspend fun getBlueprint(scenarioId: Scenario.Id): Result<Blueprint> =
        getApiBlueprint(scenarioId).map { blueprint ->
            Blueprint(
                blueprint.response.blueprint.name,
                blueprint.response.blueprint.flow.mapNotNull { it.toModule() },
                BlueprintJson(mapper.writeValueAsString(blueprint))
            )
        }

    override suspend fun setModuleData(
        scenarioId: Scenario.Id,
        moduleId: Module.Id,
        fieldName: String,
        data: Any
    ): Result<Module> =
        getApiBlueprint(scenarioId).map { blueprint ->
            blueprint.response.blueprint.flow.find { it.id == moduleId.value }?.let { flow ->
                val rootNode: ObjectNode = mapper.valueToTree(flow.mapper?.additionalProperties.orEmpty())
                val dataNode: JsonNode = mapper.valueToTree(data)
                val updatedNode: ObjectNode = rootNode.set(fieldName, dataNode.findValue(SetModuleData.Params.Content))
                val updatedProperties = mapper.readValue(updatedNode.toString(), Map::class.java)
                flow.mapper?.additionalProperties?.clear()
                flow.mapper?.additionalProperties?.putAll(
                    updatedProperties.map { (it.key.toString() to it.value) }
                )

                val response = client.patch("${config.baseUrl}/scenarios/${scenarioId.value}?confirmed=true") {
                    setHeaders()
                    setBody(
                        buildJsonObject {
                            put(SetModuleData.Params.Blueprint, mapper.writeValueAsString(blueprint.response.blueprint))
                        }.toString()
                    )
                }
                when (response.status) {
                    HttpStatusCode.OK -> flow.toModule()
                    else -> throw UnexpectedServerResponseException(response.status)
                }
            } ?: throw NotFoundException()
        }

    private suspend fun <T> get(url: String, f: suspend (HttpResponse) -> T): Result<T> =
        client.get(url) {
            setHeaders()
        }.toResult(f)

    private suspend fun <T> post(url: String, body: JsonObject, f: suspend (HttpResponse) -> T): Result<T> =
        client.post(url) {
            setHeaders()
            setBody(body.toString())
        }.toResult(f)

    private fun HttpRequestBuilder.setHeaders() {
        headers {
            append(HttpHeaders.Authorization, authToken)
        }
        contentType(ContentType.Application.Json)
    }

    private suspend fun getApiBlueprint(scenarioId: Scenario.Id): Result<com.tomaszezula.makke.api.Blueprint> =
        get("${config.baseUrl}/scenarios/${scenarioId.value}/blueprint") { response ->
            mapper.readValue(response.bodyAsText(), com.tomaszezula.makke.api.Blueprint::class.java)
        }

    private suspend fun <T> HttpResponse.toResult(f: suspend (HttpResponse) -> T): Result<T> =
        when (this.status) {
            HttpStatusCode.OK -> Result.success(f(this))
            HttpStatusCode.BadRequest -> Result.failure(BadRequestException(this.bodyAsText()))
            HttpStatusCode.NotFound -> Result.failure(NotFoundException())
            HttpStatusCode.InternalServerError -> Result.failure(ServerErrorException(this.bodyAsText()))
            else -> Result.failure(UnexpectedServerResponseException(this.status))
        }
}

