package com.extopy.domain.usecases.auth

/**
 * Burns an authorization code, so that it can only ever be exchanged once.
 */
interface DeleteAuthCodeUseCase {

    /**
     * @return True if the code was still there to burn.
     */
    suspend operator fun invoke(code: String): Boolean

}
