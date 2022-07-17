package com.tomaszezula.makker.client.model

sealed interface Scheduling

data class IndefiniteScheduling(val interval: Int): Scheduling
