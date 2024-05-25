package me.nathanfallet.extopy.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import me.nathanfallet.extopy.services.jwt.IJWTService
import org.koin.ktor.ext.inject

fun Application.configureSecurity() {
    val jwtService by inject<IJWTService>()

    authentication {
        jwt("api-v1-jwt") {
            verifier(jwtService.verifier)
            validate(jwtService.authenticationFunction)
            challenge(jwtService.challenge)
        }
    }
}
