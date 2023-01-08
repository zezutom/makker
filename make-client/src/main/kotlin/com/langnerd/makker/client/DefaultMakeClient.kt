package com.langnerd.makker.client

import com.langnerd.makker.client.extension.toApp
import com.langnerd.makker.client.extension.toAppList
import com.langnerd.makker.client.model.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.JsonObject

class DefaultMakeClient(
    private val httpClient: HttpClient,
    private val authToken: AuthToken,
    region: Region
) : MakeClient {
    private val integromatUrl = "https://${region.name.lowercase()}.make.com/api/v2/imt"

    override suspend fun getApps(filter: AppFilter?): Result<AppList> {
        return httpGet("$integromatUrl/apps") { it.toAppList(filter) }
    }

    override suspend fun getApp(appId: App.Id): Result<App> {
        return httpGet("$integromatUrl/apps/${appId.value}", JsonObject::toApp)
    }

    override suspend fun getBlueprint(): Result<Blueprint> {
        TODO("Not yet implemented")
    }

    override suspend fun copyBlueprint(blueprint: Blueprint, updates: List<ModuleUpdate>): Result<Blueprint> {
        TODO("Not yet implemented")
    }

    private suspend fun <T> httpGet(url: String, parser: (JsonObject) -> Result<T>): Result<T> {
        val res = httpClient.get(url) {
            headers {
                append(HttpHeaders.Authorization, "Token ${authToken.value}")
                append("x-imt-apps-sdk-version", "1.3.9")
            }
            contentType(ContentType.Application.Json)
        }
        return when (res.status) {
            HttpStatusCode.OK -> {
                val json: JsonObject = res.body()
                parser(json).fold(
                    { Result.success(it) },
                    { Result.failure(it) }
                )
            }

            else -> Result.failure(IllegalStateException("Invalid response code: ${res.status.value}"))
        }
    }
}