package com.langnerd.makker.extensions

import com.langnerd.makker.model.Scenario
import com.tomaszezula.makker.make.api.google_email.feeders.FeedAttachments
import com.tomaszezula.makker.make.api.google_email.feeders.TriggerNewEmail

fun TriggerNewEmail.toScenario(name: String): Scenario =
    Scenario(
        name = name,
        flow = this.toModule(1)?.let { listOf(it) } ?: listOf(),
        metadata = Scenario.Metadata.Default
    )

fun FeedAttachments.toScenario(name: String): Scenario =
    Scenario(
        name = name,
        flow = this.input.toModule(1)?.let { triggerModule ->
            val list = mutableListOf(triggerModule)
            this.toModule(2)?.let { feederModule ->
                list.add(feederModule.move(300, 0))
            }
            list
        } ?: emptyList(),
        metadata = Scenario.Metadata.Default
    )