package com.extopy.presentation.routes.auth

import com.extopy.domain.models.auth.AuthToken
import com.extopy.domain.models.auth.ClientForUser
import com.extopy.domain.usecases.auth.DeleteAuthCodeUseCase
import com.extopy.domain.usecases.auth.GenerateAuthTokenUseCase
import com.extopy.domain.usecases.auth.GetAuthCodeUseCase
import com.extopy.domain.usecases.auth.GetClientForUserForRefreshTokenUseCase
import com.extopy.domain.usecases.users.GetUserUseCase
import com.extopy.presentation.routes.RoutesTestHelper
import com.extopy.presentation.routes.RoutesTestHelper.configureTestApplication
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthApiRoutesTest {

    private class Mocks {
        val getAuthCodeUseCase = mockk<GetAuthCodeUseCase>()
        val deleteAuthCodeUseCase = mockk<DeleteAuthCodeUseCase>()
        val generateAuthTokenUseCase = mockk<GenerateAuthTokenUseCase>()
        val getClientForUserForRefreshTokenUseCase = mockk<GetClientForUserForRefreshTokenUseCase>()
        val getUserUseCase = mockk<GetUserUseCase>()
    }

    private val clientForUser = ClientForUser(RoutesTestHelper.testClient, RoutesTestHelper.testUser)
    private val token = AuthToken("access", "refresh", RoutesTestHelper.TEST_USER_ID)

    private fun ApplicationTestBuilder.configureApp(mocks: Mocks) {
        application {
            configureTestApplication(mocks.getUserUseCase)
            routing {
                authApiRoutes(
                    AuthApiRoutesDependencies(
                        mocks.getAuthCodeUseCase,
                        mocks.deleteAuthCodeUseCase,
                        mocks.generateAuthTokenUseCase,
                        mocks.getClientForUserForRefreshTokenUseCase,
                    )
                )
            }
        }
    }

    private fun authRequest(code: String = "code", secret: String = "hunter2") = """
        {"clientId":"${RoutesTestHelper.TEST_CLIENT_ID}","clientSecret":"$secret","code":"$code"}
    """.trimIndent()

    @Test
    fun testToken() = testApplication {
        val mocks = Mocks()
        coEvery { mocks.getAuthCodeUseCase("code") } returns clientForUser
        coEvery { mocks.deleteAuthCodeUseCase("code") } returns true
        coEvery { mocks.generateAuthTokenUseCase(clientForUser) } returns token
        configureApp(mocks)

        val response = client.post("/api/v1/auth/token") {
            contentType(ContentType.Application.Json)
            setBody(authRequest())
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(
            """{"accessToken":"access","refreshToken":"refresh","idToken":"${RoutesTestHelper.TEST_USER_ID}"}""",
            response.bodyAsText()
        )
    }

    /**
     * A code is good for exactly one exchange, so it is burnt before the tokens are handed out.
     */
    @Test
    fun testTokenBurnsTheCode() = testApplication {
        val mocks = Mocks()
        coEvery { mocks.getAuthCodeUseCase("code") } returns clientForUser
        coEvery { mocks.deleteAuthCodeUseCase("code") } returns true
        coEvery { mocks.generateAuthTokenUseCase(clientForUser) } returns token
        configureApp(mocks)

        client.post("/api/v1/auth/token") {
            contentType(ContentType.Application.Json)
            setBody(authRequest())
        }

        coVerify { mocks.deleteAuthCodeUseCase("code") }
    }

    @Test
    fun testTokenWithAnUnknownCode() = testApplication {
        val mocks = Mocks()
        coEvery { mocks.getAuthCodeUseCase(any()) } returns null
        configureApp(mocks)

        val response = client.post("/api/v1/auth/token") {
            contentType(ContentType.Application.Json)
            setBody(authRequest(code = "nope"))
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("""{"error":"auth_invalid_code"}""", response.bodyAsText())
    }

    /**
     * A code belongs to the client it was handed to: presenting it with someone else's secret is
     * not an exchange.
     */
    @Test
    fun testTokenWithTheWrongSecret() = testApplication {
        val mocks = Mocks()
        coEvery { mocks.getAuthCodeUseCase("code") } returns clientForUser
        configureApp(mocks)

        val response = client.post("/api/v1/auth/token") {
            contentType(ContentType.Application.Json)
            setBody(authRequest(secret = "wrong"))
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("""{"error":"auth_invalid_code"}""", response.bodyAsText())
        coVerify(exactly = 0) { mocks.deleteAuthCodeUseCase(any()) }
    }

    @Test
    fun testRefresh() = testApplication {
        val mocks = Mocks()
        coEvery { mocks.getClientForUserForRefreshTokenUseCase("refresh") } returns clientForUser
        coEvery { mocks.generateAuthTokenUseCase(clientForUser) } returns token
        configureApp(mocks)

        val response = client.post("/api/v1/auth/refresh") {
            contentType(ContentType.Application.Json)
            setBody("""{"refreshToken":"refresh"}""")
        }

        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun testRefreshWithATokenThatDoesNotHoldUp() = testApplication {
        val mocks = Mocks()
        coEvery { mocks.getClientForUserForRefreshTokenUseCase(any()) } returns null
        configureApp(mocks)

        val response = client.post("/api/v1/auth/refresh") {
            contentType(ContentType.Application.Json)
            setBody("""{"refreshToken":"nope"}""")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("""{"error":"auth_invalid_code"}""", response.bodyAsText())
    }

}
