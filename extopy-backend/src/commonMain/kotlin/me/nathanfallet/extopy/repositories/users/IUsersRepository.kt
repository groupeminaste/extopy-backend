package me.nathanfallet.extopy.repositories.users

import me.nathanfallet.extopy.models.users.CreateUserPayload
import me.nathanfallet.extopy.models.users.UpdateUserPayload
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.usecases.models.repositories.IModelSuspendRepository

interface IUsersRepository : IModelSuspendRepository<User, String, CreateUserPayload, UpdateUserPayload> {
}
