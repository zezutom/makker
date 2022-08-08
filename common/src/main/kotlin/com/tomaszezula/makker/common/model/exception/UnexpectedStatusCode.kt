package com.tomaszezula.makker.common.model.exception

import io.ktor.http.*

class UnexpectedStatusCode(statusCode: HttpStatusCode) : Exception("Unexpected status code: $statusCode")
