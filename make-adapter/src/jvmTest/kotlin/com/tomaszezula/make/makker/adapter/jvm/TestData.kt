package com.tomaszezula.make.makker.adapter.jvm

import com.tomaszezula.makker.adapter.model.Scenario

object Scenario {
    val data =
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
