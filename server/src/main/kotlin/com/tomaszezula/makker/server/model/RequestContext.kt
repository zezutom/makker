package com.tomaszezula.makker.server.model

import com.tomaszezula.makker.common.model.AuthToken

sealed interface RequestContext

object AnonymousContext : RequestContext

data class AuthenticatedContext(val token: AuthToken) : RequestContext
