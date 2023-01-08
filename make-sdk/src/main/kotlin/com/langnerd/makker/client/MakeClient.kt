package com.langnerd.makker.client

import com.langnerd.makker.model.Scenario

interface MakeClient {

    fun uploadScenario(scenario: Scenario): Result<Boolean>
}