package com.extopy.domain.repositories

import com.extopy.domain.models.application.CodeInEmail
import kotlin.time.Instant

/**
 * Reads and writes the codes sent to the email addresses being signed up.
 *
 * There is at most one code per address, so asking for a second one replaces the first.
 */
interface CodesInEmailsRepository {

    /**
     * Reads a code.
     */
    suspend fun get(code: String): CodeInEmail?

    /**
     * Stores a code for an address, replacing whichever one it already had.
     */
    suspend fun set(email: String, code: String, expiresAt: Instant): CodeInEmail?

    /**
     * Burns a code.
     */
    suspend fun delete(code: String)

}
