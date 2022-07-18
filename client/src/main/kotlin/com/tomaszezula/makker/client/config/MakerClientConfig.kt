package com.tomaszezula.makker.client.config

import com.tomaszezula.makker.client.model.AuthToken
import java.net.URI

data class MakerClientConfig(val baseUrl: URI, val token: AuthToken)
