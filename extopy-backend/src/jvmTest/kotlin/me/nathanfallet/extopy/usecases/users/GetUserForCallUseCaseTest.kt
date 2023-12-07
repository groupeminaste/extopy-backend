package me.nathanfallet.extopy.usecases.users

import io.ktor.server.application.*
import io.ktor.server.auth.jwt.*
import io.ktor.util.*
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import me.nathanfallet.extopy.models.auth.SessionPayload
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.extopy.models.users.UserContext
import me.nathanfallet.extopy.usecases.auth.IGetJWTPrincipalForCallUseCase
import me.nathanfallet.ktorx.usecases.auth.IGetSessionForCallUseCase
import me.nathanfallet.usecases.models.get.context.IGetModelWithContextSuspendUseCase
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
        val getUserUseCase = mockk<IGetModelWithContextSuspendUseCase<User, String>>()
        val useCase = GetUserForCallUseCase(getJWTPrincipalForCall, mockk(), getUserUseCase)
        val attributes = Attributes()
        val call = mockk<ApplicationCall>()
        val principal = mockk<JWTPrincipal>()
        val user = User("id", "displayName", "username")
        every { call.attributes } returns attributes
        every { getJWTPrincipalForCall(call) } returns principal
        every { principal.subject } returns "id"
        coEvery { getUserUseCase("id", UserContext("id")) } returns user
        // Fetch from repository
        assertEquals(user, useCase(call))
        // Fetch from cache
        assertEquals(user, useCase(call))
    }

    @Test
    fun invokeWithSession() = runBlocking {
        val getJWTPrincipalForCall = mockk<IGetJWTPrincipalForCallUseCase>()
        val getSessionForCallUseCase = mockk<IGetSessionForCallUseCase>()
        val getUserUseCase = mockk<IGetModelWithContextSuspendUseCase<User, String>>()
        val useCase = GetUserForCallUseCase(getJWTPrincipalForCall, getSessionForCallUseCase, getUserUseCase)
        val attributes = Attributes()
        val call = mockk<ApplicationCall>()
        val user = User("id", "displayName", "username")
        every { call.attributes } returns attributes
        every { getJWTPrincipalForCall(call) } returns null
        every { getSessionForCallUseCase(call) } returns SessionPayload("id")
        coEvery { getUserUseCase("id", UserContext("id")) } returns user
        // Fetch from repository
        assertEquals(user, useCase(call))
        // Fetch from cache
        assertEquals(user, useCase(call))
    }

    @Test
    fun invokeWithNoUserJWT() = runBlocking {
        val getJWTPrincipalForCall = mockk<IGetJWTPrincipalForCallUseCase>()
        val getUserUseCase = mockk<IGetModelWithContextSuspendUseCase<User, String>>()
        val useCase = GetUserForCallUseCase(getJWTPrincipalForCall, mockk(), getUserUseCase)
        val attributes = Attributes()
        val call = mockk<ApplicationCall>()
        val principal = mockk<JWTPrincipal>()
        every { call.attributes } returns attributes
        every { getJWTPrincipalForCall(call) } returns principal
        every { principal.subject } returns "id"
        coEvery { getUserUseCase("id", UserContext("id")) } returns null
        assertEquals(null, useCase(call))
    }

    @Test
    fun invokeWithNoUserSession() = runBlocking {
        val getJWTPrincipalForCall = mockk<IGetJWTPrincipalForCallUseCase>()
        val getSessionForCallUseCase = mockk<IGetSessionForCallUseCase>()
        val getUserUseCase = mockk<IGetModelWithContextSuspendUseCase<User, String>>()
        val useCase = GetUserForCallUseCase(getJWTPrincipalForCall, getSessionForCallUseCase, getUserUseCase)
        val attributes = Attributes()
        val call = mockk<ApplicationCall>()
        every { call.attributes } returns attributes
        every { getJWTPrincipalForCall(call) } returns null
        every { getSessionForCallUseCase(call) } returns SessionPayload("id")
        coEvery { getUserUseCase("id", UserContext("id")) } returns null
        assertEquals(null, useCase(call))
    }

}
