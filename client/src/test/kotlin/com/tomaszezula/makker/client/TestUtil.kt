package com.tomaszezula.makker.client

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.tomaszezula.makker.client.config.MakerClientConfig
import com.tomaszezula.makker.client.model.*
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.utils.io.*
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

fun <T> httpClient(payload: T): HttpClient {
    val mockEngine = MockEngine {
        respond(
            content = ByteReadChannel(mapper.writeValueAsString(payload)),
            status = HttpStatusCode.OK,
            headers = headersOf(HttpHeaders.ContentType, "application/json")
        )
    }
    return HttpClient(mockEngine)
}

fun HttpClient.mockEngine(): MockEngine = (this.engine as MockEngine)


