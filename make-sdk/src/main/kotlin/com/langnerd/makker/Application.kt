package com.langnerd.makker

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.langnerd.makker.extensions.toScenario
import com.langnerd.makker.make.api.google_email.feeders.FeedAttachments
import com.langnerd.makker.make.api.google_email.feeders.TriggerNewEmail

fun main() {
    val mapper = ObjectMapper().registerKotlinModule().setSerializationInclusion(JsonInclude.Include.NON_NULL)

    val scenario = FeedAttachments()
        .withInput(
            TriggerNewEmail()
                .withAccount(235737)
                .withFolder("SENT")
                .withSearchType(TriggerNewEmail.SearchType.SIMPLE)
                .withCriteria(TriggerNewEmail.Criteria.UNSEEN)
                .withMaxResults(5)
                .withFrom("test@acme.com")
        )
        .toScenario("Test")
    println(mapper.writeValueAsString(scenario))
}