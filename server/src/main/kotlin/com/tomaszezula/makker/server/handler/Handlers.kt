package com.tomaszezula.makker.server.handler

import com.tomaszezula.makker.common.MakeAdapter

class Handlers(makeAdapter: MakeAdapter) {
    val createScenarioHandler = CreateScenarioHandler(makeAdapter)
    val updateScenarioHandler = UpdateScenarioHandler(makeAdapter)
    val getBlueprintHandler = GetBlueprintHandler(makeAdapter)
    val setModuleDataHandler = SetModuleDataHandler(makeAdapter)
}