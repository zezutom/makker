package com.tomaszezula.makker.client

import com.tomaszezula.makker.client.handler.ScenarioHandler
import com.tomaszezula.makker.client.config.MakkerClientConfig

class MakkerClient private constructor(val scenario: ScenarioHandler) {
    companion object {
        fun invoke(config: MakkerClientConfig): MakkerClient = TODO()
    }
}

