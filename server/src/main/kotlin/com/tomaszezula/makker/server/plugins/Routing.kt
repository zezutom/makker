package com.tomaszezula.makker.server.plugins

import com.tomaszezula.makker.server.handler.*
import com.tomaszezula.makker.server.model.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory

const val ScenarioId = "scenarioId"

fun Application.configureRouting(
    handlers: Handlers,
    context: RequestContext
) {
    routing {
        get("/") {
            call.respond("Makker Server is up and running.")
        }
        route("/v1") {
            post("/scenarios") {
                handlers.createScenarioHandler.handle(context).invoke(call)
            }
            patch("/scenarios/{scenarioId}") {
                handlers.updateScenarioHandler.handle(context) { updateScenarioRequest(it) }.invoke(call)
            }
            get("/scenarios/{scenarioId}/blueprint") {
                handlers.getBlueprintHandler.handle(context) { getBlueprintRequest(it) }.invoke(call)
            }
            put("/scenarios/{scenarioId}/data") {
                handlers.setModuleDataHandler.handle(context) { setModuleDataRequest(it) }.invoke(call)
            }
        }
    }
}

private suspend fun updateScenarioRequest(call: ApplicationCall) =
    call.receive<UpdateScenarioRequest>().copy(scenarioId = call.getOrThrow(ScenarioId).toInt())

private suspend fun setModuleDataRequest(call: ApplicationCall) =
    call.receive<SetModuleDataRequest>().copy(scenarioId = call.getOrThrow(ScenarioId).toInt())

private fun getBlueprintRequest(call: ApplicationCall) =
    GetBlueprintRequest(call.getOrThrow(ScenarioId).toInt())

fun ApplicationCall.getOrThrow(param: String): String =
    this.parameters[param] ?: throw IllegalStateException("Parameter '$param' was expected, but it is missing.")

private suspend inline fun <reified T : Request> Handler<T>.handle(context: RequestContext): suspend (ApplicationCall) -> Unit =
    respond(this, context, LoggerFactory.getLogger(this::class.java)) {
        it.receive()
    }

private suspend inline fun <reified T : Request> Handler<T>.handle(
    context: RequestContext,
    crossinline f: suspend (ApplicationCall) -> T
): suspend (ApplicationCall) -> Unit = respond(this, context, LoggerFactory.getLogger(this::class.java), f)