package com.langnerd.makker

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.langnerd.makker.extensions.toScenario
import com.tomaszezula.makker.make.api.google_email.feeders.FeedAttachments
import com.tomaszezula.makker.make.api.google_email.feeders.TriggerNewEmail

fun main() {
    val mapper = ObjectMapper().registerKotlinModule().setSerializationInclusion(JsonInclude.Include.NON_NULL)
//    val makeClient = DefaultMakeClient(mapper)
//    val modules = makeClient.getModules("google-email")
//    println(modules)

    val scenario = FeedAttachments()
        .withInput(
            TriggerNewEmail()
                .withAccount(235737)
                .withSearchType(TriggerNewEmail.SearchType.SIMPLE)
                .withCriteria(TriggerNewEmail.Criteria.SEEN)
                .withMaxResults(10)
                .withFrom("test@gmail.com")
        )
        .toScenario("Test")

    val json = mapper.writeValueAsString(scenario)
    println(json)
}