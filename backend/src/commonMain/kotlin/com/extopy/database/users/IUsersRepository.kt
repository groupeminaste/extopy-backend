package com.extopy.database.users

import com.extopy.models.users.CreateUserPayload
import com.extopy.models.users.UpdateUserPayload
import com.extopy.models.users.User
import dev.kaccelero.models.UUID
import dev.kaccelero.repositories.IModelSuspendRepository

interface IUsersRepository : IModelSuspendRepository<User, UUID, CreateUserPayload, UpdateUserPayload> {

    suspend fun getForUsernameOrEmail(username: String, includePassword: Boolean): User?

}
