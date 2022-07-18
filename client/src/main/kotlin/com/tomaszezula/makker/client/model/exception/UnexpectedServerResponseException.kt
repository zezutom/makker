package com.tomaszezula.makker.client.model.exception

import io.ktor.http.*

class UnexpectedServerResponseException(responseCode: HttpStatusCode) :
    Exception("Unexpected response code: $responseCode")
