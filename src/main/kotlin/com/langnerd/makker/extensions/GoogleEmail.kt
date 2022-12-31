package com.langnerd.makker.extensions

import com.langnerd.makker.model.Module
import com.langnerd.makker.model.Scenario
import com.tomaszezula.makker.make.api.google_email.feeders.FeedAttachments
import com.tomaszezula.makker.make.api.google_email.feeders.TriggerNewEmail

fun TriggerNewEmail.toModule(id: Long): Module =
    Module(
        id = id,
        module = "google-email:TriggerNewEmail",
        version = 2,
        parameters = mapOf(
            "searchType" to this.searchType.value(),
            "maxResults" to this.maxResults,
            "folder" to this.folder,
            "criteria" to this.criteria.value(),
            "from" to this.from,
            "subject" to this.subject,
            "text" to this.text
        ),
        metadata = Module.ModuleMetadata.Default
    )

fun TriggerNewEmail.toScenario(name: String): Scenario =
    Scenario(
        name = name,
        flow = listOf(this.toModule(1)),
        metadata = Scenario.Metadata.Default
    )

fun FeedAttachments.toModule(id: Long): Module =
    Module(
        id = id,
        module = "google-email:FeedAttachments",
        version = 2,
        parameters = mapOf(
            "array" to this.array
        ),
        metadata = Module.ModuleMetadata.Default
    )

fun FeedAttachments.toScenario(name: String): Scenario =
    Scenario(
        name = name,
        flow = listOf(
            this.input.toModule(1),
            this.toModule(2).move(300, 0)
        ),
        metadata = Scenario.Metadata.Default
    )