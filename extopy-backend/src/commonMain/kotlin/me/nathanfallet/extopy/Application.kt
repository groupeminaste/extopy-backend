package me.nathanfallet.extopy

import io.ktor.server.application.*
import io.ktor.server.netty.*
import me.nathanfallet.extopy.plugins.*

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
