package com.extopy.usecases.users

import com.extopy.models.auth.SessionPayload
import com.extopy.models.users.User
import com.extopy.models.users.UserContext
import com.extopy.usecases.auth.IGetSessionForCallUseCase
import dev.kaccelero.commons.auth.IGetJWTPrincipalForCallUseCase
import dev.kaccelero.commons.repositories.IGetModelWithContextSuspendUseCase
import dev.kaccelero.models.UUID
import io.ktor.server.application.*
import io.ktor.server.auth.jwt.*
import io.ktor.util.*
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class GetUserForCallUseCaseTest {

    @Test
    fun invokeWithNothing() = runBlocking {
        val getJWTPrincipalForCall = mockk<IGetJWTPrincipalForCallUseCase>()
        val getSessionForCallUseCase = mockk<IGetSessionForCallUseCase>()
        val useCase = GetUserForCallUseCase(getJWTPrincipalForCall, getSessionForCallUseCase, mockk())
        val attributes = Attributes()
        val call = mockk<ApplicationCall>()
        every { call.attributes } returns attributes
        every { getJWTPrincipalForCall(call) } returns null
        every { getSessionForCallUseCase(call) } returns null
        assertEquals(null, useCase(call))
    }

    @Test
    fun invokeWithBahPrincipal() = runBlocking {
        val getJWTPrincipalForCall = mockk<IGetJWTPrincipalForCallUseCase>()
        val getSessionForCallUseCase = mockk<IGetSessionForCallUseCase>()
        val useCase = GetUserForCallUseCase(getJWTPrincipalForCall, getSessionForCallUseCase, mockk())
        val attributes = Attributes()
        val call = mockk<ApplicationCall>()
        val principal = mockk<JWTPrincipal>()
        every { call.attributes } returns attributes
        every { getJWTPrincipalForCall(call) } returns principal
        every { principal.subject } returns null
        every { getSessionForCallUseCase(call) } returns null
        assertEquals(null, useCase(call))
    }

    @Test
    fun invokeWithJWT() = runBlocking {
        val getJWTPrincipalForCall = mockk<IGetJWTPrincipalForCallUseCase>()
        val getUserUseCase = mockk<IGetModelWithContextSuspendUseCase<User, UUID>>()
        val useCase = GetUserForCallUseCase(getJWTPrincipalForCall, mockk(), getUserUseCase)
        val attributes = Attributes()
        val call = mockk<ApplicationCall>()
        val principal = mockk<JWTPrincipal>()
        val user = User(UUID(), "displayName", "username")
        every { call.attributes } returns attributes
        every { getJWTPrincipalForCall(call) } returns principal
        every { principal.subject } returns user.id.toString()
        coEvery { getUserUseCase(user.id, UserContext(user.id)) } returns user
        // Fetch from repository
        assertEquals(user, useCase(call))
        // Fetch from cache
        assertEquals(user, useCase(call))
    }

    @Test
    fun invokeWithSession() = runBlocking {
        val getJWTPrincipalForCall = mockk<IGetJWTPrincipalForCallUseCase>()
        val getSessionForCallUseCase = mockk<IGetSessionForCallUseCase>()
        val getUserUseCase = mockk<IGetModelWithContextSuspendUseCase<User, UUID>>()
        val useCase = GetUserForCallUseCase(getJWTPrincipalForCall, getSessionForCallUseCase, getUserUseCase)
        val attributes = Attributes()
        val call = mockk<ApplicationCall>()
        val user = User(UUID(), "displayName", "username")
        every { call.attributes } returns attributes
        every { getJWTPrincipalForCall(call) } returns null
        every { getSessionForCallUseCase(call) } returns SessionPayload(user.id)
        coEvery { getUserUseCase(user.id, UserContext(user.id)) } returns user
        // Fetch from repository
        assertEquals(user, useCase(call))
        // Fetch from cache
        assertEquals(user, useCase(call))
    }

    @Test
    fun invokeWithNoUserJWT() = runBlocking {
        val getJWTPrincipalForCall = mockk<IGetJWTPrincipalForCallUseCase>()
        val getUserUseCase = mockk<IGetModelWithContextSuspendUseCase<User, UUID>>()
        val useCase = GetUserForCallUseCase(getJWTPrincipalForCall, mockk(), getUserUseCase)
        val attributes = Attributes()
        val call = mockk<ApplicationCall>()
        val principal = mockk<JWTPrincipal>()
        val userId = UUID()
        every { call.attributes } returns attributes
        every { getJWTPrincipalForCall(call) } returns principal
        every { principal.subject } returns userId.toString()
        coEvery { getUserUseCase(userId, UserContext(userId)) } returns null
        assertEquals(null, useCase(call))
    }

    @Test
    fun invokeWithNoUserSession() = runBlocking {
        val getJWTPrincipalForCall = mockk<IGetJWTPrincipalForCallUseCase>()
        val getSessionForCallUseCase = mockk<IGetSessionForCallUseCase>()
        val getUserUseCase = mockk<IGetModelWithContextSuspendUseCase<User, UUID>>()
        val useCase = GetUserForCallUseCase(getJWTPrincipalForCall, getSessionForCallUseCase, getUserUseCase)
        val attributes = Attributes()
        val call = mockk<ApplicationCall>()
        val userId = UUID()
        every { call.attributes } returns attributes
        every { getJWTPrincipalForCall(call) } returns null
        every { getSessionForCallUseCase(call) } returns SessionPayload(userId)
        coEvery { getUserUseCase(userId, UserContext(userId)) } returns null
        assertEquals(null, useCase(call))
    }

}
