package com.extopy.infrastructure.codes

import com.extopy.domain.services.CodeGeneratorService
import java.security.SecureRandom

/**
 * Implementation of [CodeGeneratorService] drawing from a cryptographically secure source.
 *
 * The alphabet is the lowercase letters and the digits, so that a code survives being read out loud
 * and pasted back in.
 */
class RandomCodeGeneratorService : CodeGeneratorService {

    private val random = SecureRandom()

    override fun generate(): String = (1..CodeGeneratorService.CODE_LENGTH)
        .map { ALPHABET[random.nextInt(ALPHABET.length)] }
        .joinToString("")

    companion object {

        private const val ALPHABET = "abcdefghijklmnopqrstuvwxyz0123456789"

    }

}
