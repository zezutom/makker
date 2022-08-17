package com.tomaszezula.makker.server

import com.tomaszezula.makker.server.model.RequestContext
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals

class IndexTest {

    @Test
    fun success() {
        asAnonymous { index(it) }
    }

    @Test
    fun successAuthenticated() {
        asAuthenticated { index(it) }
    }

    private fun index(requestContext: RequestContext) = testApplication {
        withApplication(mockk(), requestContext)
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("\"Makker Server is up and running.\"", response.bodyAsText())
    }
}
