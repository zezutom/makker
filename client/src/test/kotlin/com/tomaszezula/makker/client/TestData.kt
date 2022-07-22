package com.tomaszezula.makker.client

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.tomaszezula.makke.api.*
import com.tomaszezula.makker.client.config.MakerClientConfig
import com.tomaszezula.makker.client.model.*
import com.tomaszezula.makker.client.model.Scenario
import java.net.URI

val config = MakerClientConfig(
    URI.create("https://test-server.com"),
    AuthToken("test-token")
)
val mapper: ObjectMapper = ObjectMapper()
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    .registerModule(KotlinModule.Builder().build())

val teamId = Team.Id(1)

val folderId = Folder.Id(2)

val scenarioId = Scenario.Id(1)

val moduleId = Module.Id(1)

val scheduling = IndefiniteScheduling(900)

val blueprint =
    BlueprintJson(
        """
        {
            "name": "New scenario",
            "flow": [
                {
                    "id": null,
                    "module": "placeholder:Placeholder",
                    "metadata": {
                        "designer": {
                            "x": 0,
                            "y": 0
                        }
                    }
                }
            ],
            "metadata": {
                "instant": false,
                "version": 1,
                "scenario": {
                    "roundtrips": 1,
                    "maxErrors": 3,
                    "autoCommit": true,
                    "autoCommitTriggerLast": true,
                    "sequential": false,
                    "confidential": false,
                    "dataloss": false,
                    "dlq": false
                },
                "designer": {
                    "orphans": []
                },
                "zone": "eu1.make.com"
            }
        }
    """.trimIndent()
    )

object MakeApi {
    fun scenario(): Scenario__1 {
        val root = Scenario__1()
        val scenario = Scenario__2()
        scenario.id = scenarioId.value
        scenario.name = "New Scenario"
        root.scenario = scenario
        return root
    }

    fun blueprint(model: Map<String, Any>? = null): com.tomaszezula.makke.api.Blueprint {
        val root = com.tomaszezula.makke.api.Blueprint()
        val response = Response()
        val blueprint = Blueprint__1()
        val flow = Flow()
        flow.id = moduleId.value
        flow.module = URI.create("json:CreateJSON")
        model?.let {
            val mapper = Mapper()
            mapper.additionalProperties.putAll(it)
            flow.mapper = mapper
        }
        blueprint.name = "My Blueprint"
        blueprint.flow = listOf(flow)
        response.blueprint = blueprint
        root.response = response
        return root
    }
}
