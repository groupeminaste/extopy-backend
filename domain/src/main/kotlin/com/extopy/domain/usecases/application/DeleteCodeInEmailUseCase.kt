package com.extopy.domain.usecases.application

/**
 * Burns a registration code, once the account it stands for has been opened.
 */
interface DeleteCodeInEmailUseCase {

    suspend operator fun invoke(code: String)

}
