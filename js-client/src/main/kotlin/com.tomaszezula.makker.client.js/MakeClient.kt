package com.tomaszezula.makker.client.js

import com.tomaszezula.makker.client.js.model.AuthToken
import com.tomaszezula.makker.client.js.model.Blueprint
import com.tomaszezula.makker.client.js.model.Scenario
import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.http.*
import kotlin.js.Promise

interface MakeClient {

    companion object {
        fun newInstance(token: String, baseUrl: Url = Url("http://localhost:8080/v1")): MakeClient =
            DefaultMakeClient(HttpClient(Js), MakeClientConfig(baseUrl, AuthToken(token)))
    }
    fun createScenario(
        teamId: Long,
        folderId: Long,
        blueprint: String,
        encoded: Boolean = false
    ): Promise<Scenario>

    fun updateScenario(
        scenarioId: Long,
        blueprint: String,
        encoded: Boolean = false
    ): Result<Scenario>

    fun getBlueprint(scenarioId: Long): Promise<Blueprint>

    fun setModuleData(
        scenarioId: Long,
        moduleId: Long,
        fieldName: String,
        data: String
    ): Result<Boolean>
}
