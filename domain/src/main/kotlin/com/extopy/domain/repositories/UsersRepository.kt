package com.extopy.domain.repositories

import com.extopy.domain.models.application.Pagination
import com.extopy.domain.models.users.CreateUserPayload
import com.extopy.domain.models.users.UpdateUserPayload
import com.extopy.domain.models.users.User
import kotlin.uuid.Uuid

/**
 * Reads and writes the accounts.
 *
 * The listing and reading methods take the user doing the reading, because what a user sees of
 * another account depends on the follow links between the two.
 */
interface UsersRepository {

    /**
     * Lists the accounts, newest first.
     *
     * @param pagination The window to read, and the terms to match.
     * @param viewedBy The user doing the reading.
     */
    suspend fun list(pagination: Pagination, viewedBy: Uuid): List<User>

    /**
     * Reads an account.
     *
     * @param id The ID of the account.
     * @param viewedBy The user doing the reading.
     */
    suspend fun get(id: Uuid, viewedBy: Uuid): User?

    /**
     * Reads an account from its username or its email address, which is what signing in goes
     * through.
     *
     * @param usernameOrEmail The username or the email address of the account.
     * @param includePassword Whether to read the password hash, which only signing in needs.
     */
    suspend fun getForUsernameOrEmail(usernameOrEmail: String, includePassword: Boolean): User?

    /**
     * Opens an account.
     *
     * @param payload The account to open, with its password already hashed.
     */
    suspend fun create(payload: CreateUserPayload): User?

    /**
     * Changes the fields of an account the payload carries.
     *
     * @return True if a row was written.
     */
    suspend fun update(id: Uuid, payload: UpdateUserPayload): Boolean

}
