package me.nathanfallet.extopy.usecases.auth

import me.nathanfallet.extopy.models.auth.RegisterCodePayload
import me.nathanfallet.extopy.models.users.CreateUserPayload
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.extopy.usecases.application.IGetCodeInEmailUseCase
import me.nathanfallet.usecases.models.create.ICreateModelSuspendUseCase

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
