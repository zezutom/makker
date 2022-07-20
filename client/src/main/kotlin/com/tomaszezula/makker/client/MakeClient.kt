package com.tomaszezula.makker.client

import com.tomaszezula.makker.client.model.*

interface MakeClient {

    /**
     * Creates a new scenario.
     *
     * @param teamId
     * @param folderId
     * @param blueprintJson
     * @param scheduling
     *
     * @return Either the created scenario or a failed result.
     */
    suspend fun createScenario(
        teamId: Team.Id,
        folderId: Folder.Id,
        blueprintJson: BlueprintJson,
        scheduling: Scheduling
    ): Result<Scenario>

    /**
     * Returns the scenario blueprint.
     *
     * @param scenarioId
     * @param draft
     *
     * @return Either the found blueprint or a failed result.
     */
    suspend fun getBlueprint(
        scenarioId: Scenario.Id,
        draft: Boolean
    ): Result<Blueprint>

    /**
     * Sets the user defined data of an arbitrary module within the provided scenario.
     *
     * @param scenarioId
     * @param moduleId
     * @param fieldName Determines the updated field.
     * @param data
     *
     * @return Either the updated module or a failed result.
     */
    suspend fun setModuleData(
        scenarioId: Scenario.Id,
        moduleId: Module.Id,
        fieldName: String,
        data: Any
    ): Result<Module>
}

