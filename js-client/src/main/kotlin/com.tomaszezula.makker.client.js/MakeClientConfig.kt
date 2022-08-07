package com.tomaszezula.makker.client.js

import com.tomaszezula.makker.adapter.model.AuthToken
import io.ktor.http.*

data class MakeClientConfig(val baseUrl: Url, val token: AuthToken)
