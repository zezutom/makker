package com.langnerd.makker.extensions

import com.langnerd.makker.model.Scenario

fun Any.toScenario(name: String): Scenario {
    return Scenario(
        name = name,
        flow = this.javaClass.declaredFields.firstOrNull { it.name == "input" }?.let { inputField ->
            inputField.trySetAccessible()
            inputField.get(this).toModule(1)?.let { inputModule ->
                val list = mutableListOf(inputModule)
                this.toModule(2)?.let { module ->
                    list.add(module.move(300, 0))
                }
                list
            } ?: emptyList()
        } ?: this.toModule(1)?.let { listOf(it) } ?: emptyList(),
        metadata = Scenario.Metadata.Default
    )
}