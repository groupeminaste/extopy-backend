package com.extopy.domain.services

/**
 * Draws the one-off codes: the ones emailed to sign up, and the OAuth authorization codes.
 */
interface CodeGeneratorService {

    /**
     * Draws a code.
     */
    fun generate(): String

    companion object {

        /**
         * How long a code is. The columns holding them are 32 characters wide.
         */
        const val CODE_LENGTH = 32

    }

}
