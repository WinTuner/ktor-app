package com.example

import com.example.routes.taskRouting
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    configureMonitoring()
    configureSerialization()
    configureRouting()
}

fun Application.configureMonitoring() {
    install(CallLogging)
}

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
    }
}

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        taskRouting()
    }
}
