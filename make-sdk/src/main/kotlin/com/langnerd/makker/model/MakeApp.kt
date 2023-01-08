package com.langnerd.makker.model

data class MakeApp(
    val name: String,
    val actions: List<Action>,
    val feeders: List<Feeder>,
    val triggers: List<Trigger>
) {
    data class Action(val name: String, val schema: String)
    data class Feeder(val name: String, val schema: String)
    data class Trigger(val name: String, val schema: String)
}
