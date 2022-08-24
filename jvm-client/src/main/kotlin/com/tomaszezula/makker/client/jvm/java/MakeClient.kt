package com.tomaszezula.makker.client.jvm.java

import com.tomaszezula.makker.client.jvm.java.model.ModuleUpdate
import com.tomaszezula.makker.client.jvm.java.model.Scenario
import com.tomaszezula.makker.common.java.Result
import com.tomaszezula.makker.common.model.Blueprint
import com.tomaszezula.makker.common.model.Scheduling
import com.tomaszezula.makker.common.model.UpdateResult
import java.nio.file.Path

interface MakeClient {

    companion object {
        fun eu(token: String): MakeClient =
            com.tomaszezula.makker.client.jvm.MakeClient.eu(token).asJava()

        fun us(token: String): MakeClient =
            com.tomaszezula.makker.client.jvm.MakeClient.us(token).asJava()
    }

    fun createScenario(
        teamId: Int,
        folderId: Int,
        blueprint: String
    ): Result<Scenario>

    fun createScenario(
        teamId: Int,
        folderId: Int,
        blueprint: String,
        scheduling: Scheduling
    ): Result<Scenario>
    fun createScenarioEncoded(
        teamId: Int,
        folderId: Int,
        blueprint: String
    ): Result<Scenario>

    fun createScenarioEncoded(
        teamId: Int,
        folderId: Int,
        blueprint: String,
        scheduling: Scheduling
    ): Result<Scenario>

    fun createScenario(
        teamId: Int,
        folderId: Int,
        filePath: Path
    ): Result<Scenario>

    fun createScenario(
        teamId: Int,
        folderId: Int,
        filePath: Path,
        scheduling: Scheduling
    ): Result<Scenario>

    fun updateScenario(
        scenarioId: Int,
        blueprint: String
    ): Result<Scenario>

    fun updateScenarioEncoded(
        scenarioId: Int,
        blueprint: String
    ): Result<Scenario>

    fun updateScenario(
        scenarioId: Int,
        filePath: Path
    ): Result<Scenario>

    fun getBlueprint(scenarioId: Int): Result<Blueprint>

    fun getBlueprints(scenarioIds: List<Int>): Result<List<Blueprint>>

    fun setModuleData(
        scenarioId: Int,
        moduleId: Int,
        key: String,
        value: String
    ): Result<UpdateResult>

    fun setModuleData(
        scenarioId: Int,
        moduleUpdates: List<ModuleUpdate>
    ): Result<UpdateResult>
}
