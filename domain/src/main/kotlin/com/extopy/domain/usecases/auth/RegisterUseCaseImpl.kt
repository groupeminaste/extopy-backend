package com.extopy.domain.usecases.auth

import com.extopy.domain.exceptions.auth.InvalidEmailCodeException
import com.extopy.domain.models.auth.RegisterCodePayload
import com.extopy.domain.models.users.CreateUserPayload
import com.extopy.domain.models.users.User
import com.extopy.domain.usecases.application.GetCodeInEmailUseCase
import com.extopy.domain.usecases.users.CreateUserUseCase

class RegisterUseCaseImpl(
    private val getCodeInEmailUseCase: GetCodeInEmailUseCase,
    private val createUserUseCase: CreateUserUseCase,
) : RegisterUseCase {

    override suspend fun invoke(code: String, payload: RegisterCodePayload): User? {
        // The address comes from the code, never from the form, so that a crafted request cannot
        // open an account on an address it never proved it owns.
        val codeInEmail = getCodeInEmailUseCase(code) ?: throw InvalidEmailCodeException()
        return createUserUseCase(
            CreateUserPayload(
                username = payload.username,
                displayName = payload.displayName,
                email = codeInEmail.email,
                password = payload.password,
                birthdate = payload.birthdate,
            )
        )
    }

}
