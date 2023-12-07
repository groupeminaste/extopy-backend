package me.nathanfallet.extopy.usecases.auth

import me.nathanfallet.extopy.models.auth.SessionPayload
import me.nathanfallet.extopy.models.users.User
import kotlin.test.Test
import kotlin.test.assertEquals

class CreateSessionForUserUseCaseTest {

    @Test
    fun testInvoke() {
        val useCase = CreateSessionForUserUseCase()
        assertEquals(
            SessionPayload("id"),
            useCase(
                User("id", "displayName", "username")
            )
        )
    }

}
