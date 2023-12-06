package me.nathanfallet.extopy.usecases.auth

import io.ktor.server.application.*
import me.nathanfallet.extopy.models.auth.RegisterCodePayload
import me.nathanfallet.extopy.models.auth.RegisterPayload
import me.nathanfallet.extopy.models.users.CreateUserPayload
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.ktorx.usecases.auth.IGetCodeRegisterUseCase
import me.nathanfallet.ktorx.usecases.auth.IRegisterUseCase
import me.nathanfallet.usecases.models.create.ICreateModelSuspendUseCase
import me.nathanfallet.usecases.users.IUser

class RegisterUseCase(
    private val getCodeRegisterUseCase: IGetCodeRegisterUseCase<RegisterPayload>,
    private val createUserUseCase: ICreateModelSuspendUseCase<User, CreateUserPayload>,
) : IRegisterUseCase<RegisterCodePayload> {

    override suspend fun invoke(input1: ApplicationCall, input2: RegisterCodePayload): IUser? {
        val code = getCodeRegisterUseCase(input1, input1.parameters["code"]!!) ?: return null
        return createUserUseCase(
            CreateUserPayload(
                username = input2.username,
                displayName = input2.displayName,
                email = code.email,
                password = input2.password,
                birthdate = input2.birthdate,
            )
        )
    }

}
