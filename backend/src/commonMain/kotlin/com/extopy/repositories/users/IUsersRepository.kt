package com.extopy.repositories.users

import com.extopy.models.users.CreateUserPayload
import com.extopy.models.users.UpdateUserPayload
import com.extopy.models.users.User
import dev.kaccelero.repositories.IModelSuspendRepository

interface IUsersRepository : IModelSuspendRepository<User, String, CreateUserPayload, UpdateUserPayload> {

    suspend fun getForUsernameOrEmail(username: String, includePassword: Boolean): User?

}
