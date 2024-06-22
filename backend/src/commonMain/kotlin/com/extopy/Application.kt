package com.extopy

import io.ktor.server.application.*
import io.ktor.server.netty.*
import com.extopy.plugins.*

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    // Initialize plugins
    configureI18n()
    configureKoin()
    configureSerialization()
    configureSecurity()
    configureSessions()
    configureTemplating()
    configureRouting()
    configureSockets()
    configureMonitoring()

    configureNotifications()
}
