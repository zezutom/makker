package com.tomaszezula.makker.common

import com.tomaszezula.makker.common.model.Blueprint
import com.tomaszezula.makker.common.model.Scenario
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

object Scenario {
    val response =
        """
            {
              "scenario": {
                "id": 925,
                "name": "New scenario",
                "teamId": 215,
                "hookId": null,
                "deviceId": null,
                "deviceScope": null,
                "concept": false,
                "description": "",
                "folderId": null,
                "isinvalid": false,
                "islinked": false,
                "islocked": false,
                "isPaused": false,
                "usedPackages": [
                  "json"
                ],
                "lastEdit": "2021-09-22T06:40:56.692Z",
                "scheduling": {
                  "type": "indefinitely",
                  "interval": 900
                },
                "iswaiting": false,
                "dlqCount": 0,
                "createdByUser": {
                  "id": 985,
                  "name": "John Doe",
                  "email": "j.doe@example.com"
                },
                "updatedByUser": {
                  "id": 986,
                  "name": "John Foo",
                  "email": "j.foo@example.com"
                }
              }
            }            
        """.trimIndent()

    val expected = Scenario(
        Scenario.Id(925),
        Scenario.TeamId(215),
        null,
        "New scenario"
    )
}

object Blueprint {
    private val blueprint =
        """
        {
              "name": "Empty integration",
              "flow": [
                {
                  "id": 2,
                  "module": "json:ParseJSON",
                  "version": 1,
                  "metadata": {
                    "designer": {
                      "x": -46,
                      "y": 47,
                      "messages": [
                        {
                          "category": "last",
                          "severity": "warning",
                          "message": "A transformer should not be the last module in the route."
                        }
                      ]
                    }
                  }
                }
              ],
              "metadata": {
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
                }
              }
        }
        """.trim()
    val response =
        """
            {
              "code": "OK",
              "response": {
                "blueprint": $blueprint,
                "scheduling": {
                  "type": "indefinitely",
                  "interval": 900
                },
                "idSequence": 4,
                "created": "2021-09-22T09:28:41.129Z",
                "last_edit": "2021-09-22T09:40:31.488Z"
              }
            }
        """.trim()

    private val blueprintJson: JsonElement = Json.parseToJsonElement(blueprint)
    val expected = Blueprint(
        "Empty integration",
        Scenario.Id(1),
        listOf(
            Blueprint.Module(Blueprint.Module.Id(2), "json:ParseJSON")
        ),
        Blueprint.Json(blueprintJson.toString())
    )
}
