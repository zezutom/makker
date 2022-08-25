package com.tomaszezula.makker.client.jvm.java

import com.tomaszezula.makker.client.jvm.MakeClient
import com.tomaszezula.makker.client.jvm.java.model.ModuleUpdate
import com.tomaszezula.makker.client.jvm.java.model.Scenario
import com.tomaszezula.makker.common.model.Scenario as CommonScenario
import com.tomaszezula.makker.common.asCompletableFuture
import com.tomaszezula.makker.common.java.Result
import com.tomaszezula.makker.common.model.Blueprint
import com.tomaszezula.makker.common.model.IndefiniteScheduling
import com.tomaszezula.makker.common.model.Scheduling
import com.tomaszezula.makker.common.model.UpdateResult
import java.nio.file.Path

class DefaultMakeClient (private val makeClient: MakeClient) :
    com.tomaszezula.makker.client.jvm.java.MakeClient {

    override fun createScenario(
        teamId: Int,
        folderId: Int,
        blueprint: String
    ): Result<Scenario> =
        Result(
            asCompletableFuture {
                makeClient.createScenario(
                    CommonScenario.TeamId(teamId),
                    CommonScenario.FolderId(folderId),
                    Blueprint.Json(blueprint),
                    IndefiniteScheduling()
                ).map { it.toModel() }
            }
        )

    override fun createScenarioEncoded(
        teamId: Int,
        folderId: Int,
        blueprint: String,
    ): Result<Scenario> =
        Result(
            asCompletableFuture {
                makeClient.createScenario(
                    CommonScenario.TeamId(teamId),
                    CommonScenario.FolderId(folderId),
                    Blueprint.Json(blueprint, true),
                    IndefiniteScheduling()
                ).map { it.toModel() }
            }
        )

    override fun createScenario(
        teamId: Int,
        folderId: Int,
        blueprint: String,
        scheduling: Scheduling
    ): Result<Scenario> =
        Result(
            asCompletableFuture {
                makeClient.createScenario(
                    CommonScenario.TeamId(teamId),
                    CommonScenario.FolderId(folderId),
                    Blueprint.Json(blueprint),
                    scheduling
                ).map { it.toModel() }
            }
        )

    override fun createScenarioEncoded(
        teamId: Int,
        folderId: Int,
        blueprint: String,
        scheduling: Scheduling
    ): Result<Scenario> =
        Result(
            asCompletableFuture {
                makeClient.createScenario(
                    CommonScenario.TeamId(teamId),
                    CommonScenario.FolderId(folderId),
                    Blueprint.Json(blueprint, true),
                    scheduling
                ).map { it.toModel() }
            }
        )

    override fun createScenario(teamId: Int, folderId: Int, filePath: Path): Result<Scenario> =
        Result(
            asCompletableFuture {
                makeClient.createScenario(
                    CommonScenario.TeamId(teamId),
                    CommonScenario.FolderId(folderId),
                    filePath,
                    IndefiniteScheduling()
                ).map { it.toModel() }
            }
        )

    override fun createScenario(
        teamId: Int,
        folderId: Int,
        filePath: Path,
        scheduling: Scheduling
    ): Result<Scenario> =
        Result(
            asCompletableFuture {
                makeClient.createScenario(
                    CommonScenario.TeamId(teamId),
                    CommonScenario.FolderId(folderId),
                    filePath,
                    scheduling
                ).map { it.toModel() }
            }
        )

    override fun updateScenario(scenarioId: Int, blueprint: String): Result<Scenario> =
        Result(
            asCompletableFuture {
                makeClient.updateScenario(
                    CommonScenario.Id(scenarioId),
                    Blueprint.Json(blueprint)
                ).map { it.toModel() }
            }
        )

    override fun updateScenarioEncoded(scenarioId: Int, blueprint: String): Result<Scenario> =
        Result(
            asCompletableFuture {
                makeClient.updateScenario(
                    CommonScenario.Id(scenarioId),
                    Blueprint.Json(blueprint, true)
                ).map { it.toModel() }
            }
        )

    override fun updateScenario(scenarioId: Int, filePath: Path): Result<Scenario> =
        Result(
            asCompletableFuture {
                makeClient.updateScenario(
                    CommonScenario.Id(scenarioId),
                    filePath
                ).map { it.toModel() }
            }
        )

    override fun getBlueprint(scenarioId: Int): Result<Blueprint> =
        Result(
            asCompletableFuture {
                makeClient.getBlueprint(CommonScenario.Id(scenarioId))
            }
        )

    override fun getBlueprints(scenarioIds: List<Int>): Result<List<Blueprint>> =
        Result(
            asCompletableFuture {
                makeClient.getBlueprints(scenarioIds.map { CommonScenario.Id(it) })
            }
        )

    override fun setModuleData(
        scenarioId: Int,
        moduleId: Int,
        key: String,
        value: String
    ): Result<UpdateResult> =
        Result(
            asCompletableFuture {
                makeClient.setModuleData(
                    CommonScenario.Id(scenarioId),
                    Blueprint.Module.Id(moduleId),
                    key,
                    value
                )
            }
        )

    override fun setModuleData(
        scenarioId: Int,
        moduleUpdates: List<ModuleUpdate>
    ): Result<UpdateResult> =
        Result(
            asCompletableFuture {
                makeClient.setModuleData(CommonScenario.Id(scenarioId), moduleUpdates.map {
                    com.tomaszezula.makker.client.jvm.model.ModuleUpdate(
                        Blueprint.Module.Id(it.moduleId), it.key, it.value
                    )
                })
            }
        )
}
