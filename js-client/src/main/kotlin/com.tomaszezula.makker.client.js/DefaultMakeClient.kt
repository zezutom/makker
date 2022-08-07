package com.tomaszezula.makker.client.js

import com.tomaszezula.makker.client.js.model.Blueprint
import com.tomaszezula.makker.client.js.model.Scenario
import io.ktor.client.*
import kotlin.js.Promise

class DefaultMakeClient(client: HttpClient, config: MakeClientConfig) : MakeClient {
    override fun createScenario(teamId: Long, folderId: Long, blueprint: String, encoded: Boolean): Promise<Scenario> {
        TODO("Not yet implemented")
    }

    override fun updateScenario(scenarioId: Long, blueprint: String, encoded: Boolean): Result<Scenario> {
        TODO("Not yet implemented")
    }

    override fun getBlueprint(scenarioId: Long): Promise<Blueprint> {
        TODO("Not yet implemented")
    }

    override fun setModuleData(scenarioId: Long, moduleId: Long, fieldName: String, data: String): Result<Boolean> {
        TODO("Not yet implemented")
    }
}