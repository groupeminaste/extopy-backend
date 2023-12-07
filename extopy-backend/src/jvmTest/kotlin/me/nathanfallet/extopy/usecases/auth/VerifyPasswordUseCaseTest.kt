package me.nathanfallet.extopy.usecases.auth

import at.favre.lib.crypto.bcrypt.BCrypt
import kotlin.test.Test
import kotlin.test.assertTrue

class VerifyPasswordUseCaseTest {

    @Test
    fun invoke() {
        val useCase = VerifyPasswordUseCase()
        assertTrue(useCase("password", BCrypt.withDefaults().hashToString(12, "password".toCharArray())))
    }

}
