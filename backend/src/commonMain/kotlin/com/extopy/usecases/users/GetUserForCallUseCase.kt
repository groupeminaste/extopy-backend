package com.extopy.usecases.users

import com.extopy.models.users.User
import com.extopy.models.users.UserContext
import com.extopy.usecases.auth.IGetSessionForCallUseCase
import dev.kaccelero.commons.auth.IGetJWTPrincipalForCallUseCase
import dev.kaccelero.commons.repositories.IGetModelWithContextSuspendUseCase
import dev.kaccelero.commons.users.IGetUserForCallUseCase
import dev.kaccelero.models.IUser
import io.ktor.server.application.*
import io.ktor.util.*

class GetUserForCallUseCase(
    private val getJWTPrincipalForCall: IGetJWTPrincipalForCallUseCase,
    private val getSessionForCallUseCase: IGetSessionForCallUseCase,
    private val getUserUseCase: IGetModelWithContextSuspendUseCase<User, String>,
) : IGetUserForCallUseCase {

    private data class UserForCall(
        val user: User?,
    )

    private val userKey = AttributeKey<UserForCall>("user")

    override suspend fun invoke(input: ApplicationCall): IUser? {
        // Note: we cannot use `computeIfAbsent` because it does not support suspending functions
        return input.attributes.getOrNull(userKey)?.user ?: run {
            val id = getJWTPrincipalForCall(input)?.subject ?: getSessionForCallUseCase(input)?.userId
            val computed = UserForCall(id?.let { getUserUseCase(it, UserContext(it)) })
            input.attributes.put(userKey, computed)
            computed.user
        }
    }

}
