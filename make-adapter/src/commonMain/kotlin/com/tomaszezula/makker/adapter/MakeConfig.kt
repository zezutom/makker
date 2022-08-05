package com.tomaszezula.makker.adapter

import io.ktor.http.*

data class MakeConfig(val baseUrl: Url) {
    companion object {
        enum class Zone(val value: String) {
            EU("eu1"), US("us1")
        }
        fun eu(): MakeConfig = makeClientConfig(Zone.EU)
        fun us(): MakeConfig = makeClientConfig(Zone.US)

        private fun makeClientConfig(zone: Zone): MakeConfig =
            MakeConfig(
                Url("https://${zone.value}.make.com/api/v2")
            )
    }
}
