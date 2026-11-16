package com.extopy.domain.usecases.application

import com.extopy.domain.models.application.CodeInEmail

/**
 * Resolves the address a registration code was sent to.
 */
interface GetCodeInEmailUseCase {

    /**
     * @return The code, or null if it is unknown or has expired.
     */
    suspend operator fun invoke(code: String): CodeInEmail?

}
