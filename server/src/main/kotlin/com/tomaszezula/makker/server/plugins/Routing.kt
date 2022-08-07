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
            getBlueprintHandler.handle(call, context) {
                it.parameters["scenarioId"]?.let { scenarioId -> GetBlueprintRequest(scenarioId.toLong()) }
                    ?: throw BadRequestException("Missing or invalid input: scenarioId")
            }
        }
        post("/scenario") {
            createScenarioHandler.handle(call, context)
        }
        put("/scenario") {
            updateScenarioHandler.handle(call, context)
        }
        put("/module") {
            setModuleDataHandler.handle(call, context)
        }
    }
}

private suspend inline fun <reified T : Request> Handler<T>.handle(
    call: ApplicationCall,
    context: RequestContext
) = call.respond(this, context, LoggerFactory.getLogger(this::class.java)) {
    it.receive()
}

private suspend inline fun <reified T : Request> Handler<T>.handle(
    call: ApplicationCall,
    context: RequestContext,
    f: (ApplicationCall) -> T
) = call.respond(this, context, LoggerFactory.getLogger(this::class.java), f)