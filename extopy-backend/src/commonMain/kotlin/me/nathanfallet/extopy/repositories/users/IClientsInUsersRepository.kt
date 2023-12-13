package me.nathanfallet.extopy.repositories.users

import kotlinx.datetime.Instant
import me.nathanfallet.extopy.models.users.ClientInUser

interface IClientsInUsersRepository {

    suspend fun create(userId: String, clientId: String, expiration: Instant): ClientInUser?
    suspend fun get(code: String): ClientInUser?
    suspend fun delete(code: String): Boolean

}
