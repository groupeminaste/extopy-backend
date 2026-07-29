package com.extopy

import com.extopy.domain.di.domainModule
import com.extopy.infrastructure.config.configureSecurity
import com.extopy.infrastructure.config.configureSentry
import com.extopy.infrastructure.di.infrastructureModule
import com.extopy.presentation.config.*
import com.extopy.presentation.di.presentationModule
import io.ktor.server.application.*
import io.ktor.server.netty.*
import org.koin.ktor.plugin.Koin

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    // Initialize plugins
    install(Koin) {
        modules(
            domainModule(environment.config.property("extopy.baseUrl").getString()),
            presentationModule,
            infrastructureModule,
        )
    }
    configureSerialization()
    configureTemplating()
    configureErrorHandling()
    configureSecurity()
    configureSessions()
    configureRouting()
    configureMonitoring()
    configureSentry()
}
