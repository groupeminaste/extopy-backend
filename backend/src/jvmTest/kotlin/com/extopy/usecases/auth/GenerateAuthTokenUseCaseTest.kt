package com.extopy.usecases.auth

import com.extopy.models.application.Client
import com.extopy.models.auth.AuthToken
import com.extopy.models.auth.ClientForUser
import com.extopy.models.users.User
import com.extopy.services.jwt.IJWTService
import dev.kaccelero.models.UUID
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class GenerateAuthTokenUseCaseTest {

    @Test
    fun testInvoke() = runBlocking {
        val service = mockk<IJWTService>()
        val userCase = GenerateAuthTokenUseCase(service)
        val clientId = UUID()
        val userId = UUID()
        every { service.generateJWT(userId, clientId, "access") } returns "accessToken"
        every { service.generateJWT(userId, clientId, "refresh") } returns "refreshToken"
        assertEquals(
            AuthToken(
                "accessToken",
                "refreshToken",
                userId
            ),
            userCase(
                ClientForUser(
                    Client(clientId, UUID(), "name", "description", "secret", "redirect"),
                    User(userId, "displayName", "username")
                )
            )
        )
    }

}
