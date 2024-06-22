package com.extopy.usecases.auth

import com.extopy.models.auth.RegisterCodePayload
import com.extopy.models.users.CreateUserPayload
import com.extopy.models.users.User
import com.extopy.usecases.application.IGetCodeInEmailUseCase
import dev.kaccelero.commons.repositories.ICreateModelSuspendUseCase

class RegisterUseCase(
    private val getCodeInEmailUseCase: IGetCodeInEmailUseCase,
    private val createUserUseCase: ICreateModelSuspendUseCase<User, CreateUserPayload>,
) : IRegisterUseCase {

    override suspend fun invoke(input1: String, input2: RegisterCodePayload): User? {
        val codePayload = getCodeInEmailUseCase(input1) ?: return null
        return createUserUseCase(
            CreateUserPayload(
                username = input2.username,
                displayName = input2.displayName,
                email = codePayload.email,
                password = input2.password,
                birthdate = input2.birthdate,
            )
        )
    }

}
