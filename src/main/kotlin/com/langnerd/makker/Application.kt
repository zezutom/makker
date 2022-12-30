package com.langnerd.makker

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.langnerd.makker.client.DefaultMakeClient
import com.langnerd.makker.model.Module
import com.langnerd.makker.model.Scenario
import com.tomaszezula.makker.make.api.google_email.feeders.TriggerNewEmail

fun main() {
    val mapper = ObjectMapper().registerKotlinModule().setSerializationInclusion(JsonInclude.Include.NON_NULL)
    val makeClient = DefaultMakeClient(mapper)

    val triggerNewEmail = TriggerNewEmail()
        .withSearchType(TriggerNewEmail.SearchType.SIMPLE)
        .withCriteria(TriggerNewEmail.Criteria.SEEN)
        .withMaxResults(10)
        .withFrom("zezulatomas@gmail.com")

    val scenario = Scenario(
        name = "Test",
        flow = listOf(
            Module(
                id = 1,
                module = "google-email:TriggerNewEmail",
                version = 2,
                parameters = mapOf( // TODO extension function "class to map" => triggerNewEmail.toMap()
                    "searchType" to triggerNewEmail.searchType.name,
                    "maxResults" to triggerNewEmail.maxResults,
                    "folder" to triggerNewEmail.folder,
                    "criteria" to triggerNewEmail.criteria.name,
                    "from" to triggerNewEmail.from,
                    "subject" to triggerNewEmail.subject,
                    "text" to triggerNewEmail.text
                ),
                metadata = Module.ModuleMetadata(
                    designer = Module.ModuleMetadata.Designer(x = 0, y = 0)
                )
            )
        ),
        metadata = Scenario.Metadata(
            instant = false,
            version = 1,
            scenario = Scenario.Metadata.ScenarioMetadata(
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
    )
    val json = mapper.writeValueAsString(scenario)
    println(json)
//    val modules = makeClient.getModules("google-email")
//    println(modules)
}