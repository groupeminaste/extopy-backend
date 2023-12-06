package me.nathanfallet.extopy.usecases.users

import io.ktor.server.application.*
import me.nathanfallet.ktorx.usecases.users.IGetUserForCallUseCase
import me.nathanfallet.usecases.users.IUser

class GetUserForCallUseCase : IGetUserForCallUseCase {

    override suspend fun invoke(input: ApplicationCall): IUser? {
        return null
    }

}
