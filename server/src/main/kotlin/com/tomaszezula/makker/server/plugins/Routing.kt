package com.tomaszezula.makker.server.plugins

import com.tomaszezula.makker.server.handler.*
import com.tomaszezula.makker.server.model.GetBlueprintRequest
import com.tomaszezula.makker.server.model.Request
import com.tomaszezula.makker.server.model.RequestContext
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory

fun Application.configureRouting(
    createScenarioHandler: CreateScenarioHandler,
    updateScenarioHandler: UpdateScenarioHandler,
    getBlueprintHandler: GetBlueprintHandler,
    setModuleDataHandler: SetModuleDataHandler,
    context: RequestContext
) {
    routing {
        get("/blueprint/{scenarioId}") {
            getBlueprintHandler.handle(context) {
                it.parameters["scenarioId"]?.let { scenarioId -> GetBlueprintRequest(scenarioId.toLong()) }
                    ?: throw BadRequestException("Missing or invalid input: scenarioId")
            }.invoke(call)
        }
        post("/scenario") {
            createScenarioHandler.handle(context).invoke(call)
        }
        put("/scenario") {
            updateScenarioHandler.handle(context).invoke(call)
        }
        put("/module") {
            setModuleDataHandler.handle(context).invoke(call)
        }
    }
}

private suspend inline fun <reified T : Request> Handler<T>.handle(context: RequestContext): suspend (ApplicationCall) -> Unit =
    respond(this, context, LoggerFactory.getLogger(this::class.java)) {
        it.receive()
    }

private suspend inline fun <reified T : Request> Handler<T>.handle(
    context: RequestContext,
    crossinline f: suspend (ApplicationCall) -> T
): suspend (ApplicationCall) -> Unit = respond(this, context, LoggerFactory.getLogger(this::class.java), f)