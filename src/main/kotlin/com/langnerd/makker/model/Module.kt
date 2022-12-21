package com.langnerd.makker.model

sealed interface Module {
    val name: String
    val jsonSchema: String
}

data class Action(override val name: String, override val jsonSchema: String) : Module

data class Feeder(override val name: String, override val jsonSchema: String, val dependsOn: Trigger) : Module

data class Trigger(override val name: String, override val jsonSchema: String) : Module
