package com.extopy.infrastructure.bcrypt

import at.favre.lib.crypto.bcrypt.BCrypt
import com.extopy.domain.services.PasswordEncoderService

/**
 * Implementation of [PasswordEncoderService] using BCrypt.
 *
 * The cost is the one the deployed hashes were written with, so the accounts that already exist keep
 * signing in.
 */
class BCryptPasswordEncoderService : PasswordEncoderService {

    override fun encode(rawPassword: String): String =
        BCrypt.withDefaults().hashToString(COST, rawPassword.toCharArray())

    override fun matches(rawPassword: String, encodedPassword: String): Boolean =
        BCrypt.verifyer().verify(rawPassword.toCharArray(), encodedPassword).verified

    companion object {

        private const val COST = 12

    }

}
