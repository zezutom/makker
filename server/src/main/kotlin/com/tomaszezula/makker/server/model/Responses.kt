package com.tomaszezula.makker.server.model

sealed interface Response

data class Ok(val data: Any) : Response

data class Created(val data: Any) : Response
