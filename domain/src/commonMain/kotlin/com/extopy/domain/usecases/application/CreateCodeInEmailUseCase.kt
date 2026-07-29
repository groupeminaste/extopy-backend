package com.extopy.domain.usecases.application

import com.extopy.domain.models.application.CodeInEmail

/**
 * Draws the code emailed to an address to prove it is reachable, replacing whichever one that
 * address already had.
 */
interface CreateCodeInEmailUseCase {

    /**
     * @return The code that was stored.
     */
    suspend operator fun invoke(email: String): CodeInEmail?

}
