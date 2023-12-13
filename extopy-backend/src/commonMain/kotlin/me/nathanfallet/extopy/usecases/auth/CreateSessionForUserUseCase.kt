package me.nathanfallet.extopy.usecases.auth

import me.nathanfallet.extopy.models.auth.SessionPayload
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.ktorx.usecases.auth.ICreateSessionForUserUseCase
import me.nathanfallet.usecases.auth.ISessionPayload
import me.nathanfallet.usecases.users.IUser

class CreateSessionForUserUseCase : ICreateSessionForUserUseCase {

    override fun invoke(input: IUser): ISessionPayload {
        return SessionPayload((input as User).id)
    }

}
