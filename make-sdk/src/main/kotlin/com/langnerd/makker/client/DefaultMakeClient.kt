package com.langnerd.makker.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.langnerd.makker.model.Scenario

class DefaultMakeClient(private val mapper: ObjectMapper) : MakeClient {
    override fun uploadScenario(scenario: Scenario): Result<Boolean> {
        TODO("Not yet implemented")
    }
}