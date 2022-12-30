package com.langnerd.makker.model

data class Scenario(val name: String, val flow: List<Module>, val metadata: Metadata) {
    data class Metadata(val instant: Boolean, val version: Int, val scenario: ScenarioMetadata) {
        data class ScenarioMetadata(
            val roundtrips: Int,
            val maxErrors: Int,
            val autoCommit: Boolean,
            val autoCommitTriggerLast: Boolean,
            val sequential: Boolean,
            val confidential: Boolean,
            val dataloss: Boolean,
            val dlq: Boolean,
            val freshVariables: Boolean,
        )
    }
}
