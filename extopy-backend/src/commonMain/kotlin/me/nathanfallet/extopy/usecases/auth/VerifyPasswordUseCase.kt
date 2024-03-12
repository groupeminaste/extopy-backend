package me.nathanfallet.extopy.usecases.auth

import at.favre.lib.crypto.bcrypt.BCrypt

class VerifyPasswordUseCase : IVerifyPasswordUseCase {

    override fun invoke(input1: String, input2: String): Boolean =
        BCrypt.verifyer().verify(input1.toCharArray(), input2).verified

}
