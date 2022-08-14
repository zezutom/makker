package com.tomaszezula.makker.client.jvm

import com.tomaszezula.makker.client.jvm.model.ModuleUpdate
import com.tomaszezula.makker.common.DefaultMakeAdapter
import com.tomaszezula.makker.common.MakeConfig
import com.tomaszezula.makker.common.model.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import kotlinx.serialization.json.Json
import java.nio.file.Path

interface MakeClient {

    companion object {
        fun eu(token: String): MakeClient =
            newInstance(MakeConfig.eu(), token)

        fun us(token: String): MakeClient =
            newInstance(MakeConfig.us(), token)

        private fun newInstance(config: MakeConfig, token: String): MakeClient =
            DefaultMakeClient(
                DefaultMakeAdapter(
                    config,
                    HttpClient(CIO),
                    Json { ignoreUnknownKeys = true }
                ), AuthToken(token)
            )
    }

    fun asJava(): JavaMakeClient = DefaultJavaMakeClient(this)

    suspend fun createScenario(
        teamId: Scenario.TeamId,
        folderId: Scenario.FolderId,
        blueprint: Blueprint.Json,
        scheduling: Scheduling = IndefiniteScheduling(),
    ): Result<Scenario>

    suspend fun createScenario(
        teamId: Scenario.TeamId,
        folderId: Scenario.FolderId,
        filePath: Path,
        scheduling: Scheduling = IndefiniteScheduling(),
    ): Result<Scenario>


    suspend fun updateScenario(
        scenarioId: Scenario.Id,
        blueprint: Blueprint.Json
    ): Result<Scenario>

    suspend fun updateScenario(
        scenarioId: Scenario.Id,
        filePath: Path
    ): Result<Scenario>

    suspend fun getBlueprint(scenarioId: Scenario.Id): Result<Blueprint>

    suspend fun getBlueprints(scenarioIds: List<Scenario.Id>): Result<List<Blueprint>>

    suspend fun setModuleData(
        scenarioId: Scenario.Id,
        moduleId: Blueprint.Module.Id,
        key: String,
        value: String
    ): Result<UpdateResult>

    suspend fun setModuleData(
        scenarioId: Scenario.Id,
        moduleUpdates: List<ModuleUpdate>
    ): Result<UpdateResult>
}

