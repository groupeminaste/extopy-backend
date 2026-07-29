package com.extopy.domain.services

/**
 * Hashes and checks the passwords.
 */
interface PasswordEncoderService {

    /**
     * Hashes a password.
     */
    fun encode(rawPassword: String): String

    /**
     * Checks a password against a hash.
     */
    fun matches(rawPassword: String, encodedPassword: String): Boolean

}
