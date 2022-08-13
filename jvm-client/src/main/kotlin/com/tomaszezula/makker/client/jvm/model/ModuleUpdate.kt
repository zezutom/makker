package com.tomaszezula.makker.client.jvm.model

import com.tomaszezula.makker.common.model.Blueprint

data class ModuleUpdate(val moduleId: Blueprint.Module.Id, val key: String, val value: String)
