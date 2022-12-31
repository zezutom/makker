package com.langnerd.makker.model

data class Scenario(val name: String, val flow: List<Module>, val metadata: Metadata) {
    data class Metadata(val instant: Boolean, val version: Int, val scenario: ScenarioMetadata) {
        companion object {
            val Default = Metadata(
                instant = false,
                version = 1,
                scenario = ScenarioMetadata(
                    roundtrips = 1,
                    maxErrors = 3,
                    autoCommit = false,
                    autoCommitTriggerLast = false,
                    sequential = false,
                    confidential = false,
                    dataloss = false,
                    dlq = false,
                    freshVariables = false
                )
            )
        }
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
