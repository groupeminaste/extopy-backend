package com.extopy.domain.usecases.auth

import com.extopy.domain.exceptions.auth.InvalidEmailCodeException
import com.extopy.domain.models.application.Client
import com.extopy.domain.models.application.CodeInEmail
import com.extopy.domain.models.auth.ClientForUser
import com.extopy.domain.models.auth.RegisterCodePayload
import com.extopy.domain.models.auth.TokenPayload
import com.extopy.domain.models.auth.TokenType
import com.extopy.domain.models.users.ClientInUser
import com.extopy.domain.models.users.CreateUserPayload
import com.extopy.domain.models.users.User
import com.extopy.domain.repositories.ClientsInUsersRepository
import com.extopy.domain.services.CodeGeneratorService
import com.extopy.domain.services.TokenService
import com.extopy.domain.usecases.application.GetCodeInEmailUseCase
import com.extopy.domain.usecases.users.CreateUserUseCase
import com.extopy.domain.usecases.users.GetUserUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.uuid.Uuid

class AuthUseCasesTest {

    private val userId = Uuid.parse("00000000-0000-4000-8000-000000000001")
    private val clientId = Uuid.parse("00000000-0000-4000-8000-000000000002")

    private val user = User(id = userId, displayName = "Nathan Fallet", username = "nathanfallet")

    private val client = Client(
        id = clientId,
        ownerId = userId,
        name = "Extopy for iOS",
        description = "The one on your phone",
        secret = "hunter2",
        redirectUri = "extopy://auth?code={code}",
    )

    /**
     * The address comes from the code, never from the form, so that a crafted request cannot open an
     * account on an address it never proved it owns.
     */
    @Test
    fun testRegisteringUsesTheAddressOfTheCode() = runTest {
        val getCodeInEmailUseCase = mockk<GetCodeInEmailUseCase>()
        val createUserUseCase = mockk<CreateUserUseCase>()
        val written = slot<CreateUserPayload>()
        coEvery { getCodeInEmailUseCase("abc") } returns
                CodeInEmail("proven@extopy.com", "abc", Clock.System.now() + 1.hours)
        coEvery { createUserUseCase(capture(written)) } returns user

        RegisterUseCaseImpl(getCodeInEmailUseCase, createUserUseCase)(
            "abc",
            RegisterCodePayload("hunter2", "nathanfallet", "Nathan Fallet", LocalDate.parse("2002-12-24"))
        )

        assertEquals("proven@extopy.com", written.captured.email)
    }

    @Test
    fun testRegisteringWithAnUnknownCode() = runTest {
        val getCodeInEmailUseCase = mockk<GetCodeInEmailUseCase>()
        val createUserUseCase = mockk<CreateUserUseCase>()
        coEvery { getCodeInEmailUseCase(any()) } returns null

        assertFailsWith<InvalidEmailCodeException> {
            RegisterUseCaseImpl(getCodeInEmailUseCase, createUserUseCase)(
                "nope",
                RegisterCodePayload("hunter2", "nathanfallet", "Nathan Fallet", LocalDate.parse("2002-12-24"))
            )
        }
    }

    @Test
    fun testDrawingAnAuthorizationCode() = runTest {
        val repository = mockk<ClientsInUsersRepository>()
        val codeGeneratorService = mockk<CodeGeneratorService>()
        every { codeGeneratorService.generate() } returns "drawn"
        coEvery { repository.create("drawn", userId, clientId, any()) } returns
                ClientInUser("drawn", userId, clientId, Clock.System.now() + 1.hours)

        val result = CreateAuthCodeUseCaseImpl(repository, codeGeneratorService)(ClientForUser(client, user))

        assertEquals("drawn", result)
    }

    @Test
    fun testReadingAnAuthorizationCode() = runTest {
        val repository = mockk<ClientsInUsersRepository>()
        val getClientUseCase = mockk<GetClientUseCase>()
        val getUserUseCase = mockk<GetUserUseCase>()
        coEvery { repository.get("code") } returns
                ClientInUser("code", userId, clientId, Clock.System.now() + 1.hours)
        coEvery { getClientUseCase(clientId) } returns client
        coEvery { getUserUseCase(userId, userId) } returns user

        val result = GetAuthCodeUseCaseImpl(repository, getClientUseCase, getUserUseCase)("code")

        assertEquals(ClientForUser(client, user), result)
    }

    /**
     * A code that sat around for too long is no longer a code.
     */
    @Test
    fun testAnExpiredAuthorizationCode() = runTest {
        val repository = mockk<ClientsInUsersRepository>()
        val getClientUseCase = mockk<GetClientUseCase>()
        val getUserUseCase = mockk<GetUserUseCase>()
        coEvery { repository.get("code") } returns
                ClientInUser("code", userId, clientId, Clock.System.now() - 1.hours)

        val result = GetAuthCodeUseCaseImpl(repository, getClientUseCase, getUserUseCase)("code")

        assertNull(result)
    }

    @Test
    fun testIssuingThePairOfTokens() = runTest {
        val tokenService = mockk<TokenService>()
        every { tokenService.generateToken(userId, clientId, TokenType.ACCESS) } returns "access"
        every { tokenService.generateToken(userId, clientId, TokenType.REFRESH) } returns "refresh"

        val result = GenerateAuthTokenUseCaseImpl(tokenService)(ClientForUser(client, user))

        assertEquals("access", result.accessToken)
        assertEquals("refresh", result.refreshToken)
        assertEquals(userId, result.idToken)
    }

    @Test
    fun testTradingARefreshTokenBack() = runTest {
        val tokenService = mockk<TokenService>()
        val getUserUseCase = mockk<GetUserUseCase>()
        val getClientUseCase = mockk<GetClientUseCase>()
        every { tokenService.verifyToken("refresh") } returns TokenPayload(userId, clientId)
        coEvery { getUserUseCase(userId, userId) } returns user
        coEvery { getClientUseCase(clientId) } returns client

        val result = GetClientForUserForRefreshTokenUseCaseImpl(tokenService, getUserUseCase, getClientUseCase)(
            "refresh"
        )

        assertEquals(ClientForUser(client, user), result)
    }

    @Test
    fun testARefreshTokenThatDoesNotHoldUp() = runTest {
        val tokenService = mockk<TokenService>()
        val getUserUseCase = mockk<GetUserUseCase>()
        val getClientUseCase = mockk<GetClientUseCase>()
        every { tokenService.verifyToken(any()) } returns null

        val result = GetClientForUserForRefreshTokenUseCaseImpl(tokenService, getUserUseCase, getClientUseCase)("nope")

        assertNull(result)
    }

    /**
     * A token issued to no client in particular cannot be traded back for one.
     */
    @Test
    fun testARefreshTokenWithNoClient() = runTest {
        val tokenService = mockk<TokenService>()
        val getUserUseCase = mockk<GetUserUseCase>()
        val getClientUseCase = mockk<GetClientUseCase>()
        every { tokenService.verifyToken("refresh") } returns TokenPayload(userId, null)

        val result = GetClientForUserForRefreshTokenUseCaseImpl(tokenService, getUserUseCase, getClientUseCase)(
            "refresh"
        )

        assertNull(result)
    }

}
