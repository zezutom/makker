package com.tomaszezula.makker.client.jvm.java

import com.tomaszezula.makker.client.jvm.model.ModuleUpdate
import com.tomaszezula.makker.common.model.Scenario
import com.tomaszezula.makker.client.jvm.java.model.Scenario as JavaScenario
import com.tomaszezula.makker.client.jvm.java.model.ModuleUpdate as JavaModuleUpdate

fun Scenario.toModel(): JavaScenario =
    JavaScenario(this.id.value, this.teamId.value, this.folderId?.value, this.name)

fun ModuleUpdate.toModel(): JavaModuleUpdate =
    JavaModuleUpdate(this.moduleId.value, this.key, this.value)
