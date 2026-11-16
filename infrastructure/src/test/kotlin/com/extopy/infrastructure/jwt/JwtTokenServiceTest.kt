package com.extopy.infrastructure.jwt

import com.extopy.domain.models.auth.TokenType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.uuid.Uuid

class JwtTokenServiceTest {

    private val userId = Uuid.parse("00000000-0000-4000-8000-000000000001")
    private val clientId = Uuid.parse("00000000-0000-4000-8000-000000000002")

    private val service = JwtTokenService("secret", "extopy")

    @Test
    fun testATokenSaysWhoItIsForAndWhoItWasIssuedTo() {
        val token = service.generateToken(userId, clientId, TokenType.ACCESS)

        val payload = service.verifyToken(token)

        assertEquals(userId, payload?.userId)
        assertEquals(clientId, payload?.clientId)
    }

    @Test
    fun testTheTwoKindsOfTokenAreNotTheSame() {
        assertNotEquals(
            service.generateToken(userId, clientId, TokenType.ACCESS),
            service.generateToken(userId, clientId, TokenType.REFRESH)
        )
    }

    /**
     * A token signed with another secret, or by another issuer, is not one of ours.
     */
    @Test
    fun testATokenFromSomewhereElse() {
        val other = JwtTokenService("other-secret", "extopy")
        assertNull(service.verifyToken(other.generateToken(userId, clientId, TokenType.ACCESS)))

        val elsewhere = JwtTokenService("secret", "somewhere-else")
        assertNull(service.verifyToken(elsewhere.generateToken(userId, clientId, TokenType.ACCESS)))
    }

    @Test
    fun testSomethingThatIsNotAToken() {
        assertNull(service.verifyToken("nope"))
        assertNull(service.verifyToken(""))
    }

}
