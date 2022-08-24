package com.tomaszezula.makker.client.jvm.java

import com.tomaszezula.makker.common.model.Scenario
import com.tomaszezula.makker.client.jvm.java.model.Scenario as JavaScenario

fun Scenario.toModel(): JavaScenario =
    JavaScenario(this.id.value, this.teamId.value, this.folderId?.value, this.name)
