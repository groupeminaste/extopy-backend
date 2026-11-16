package com.extopy.domain.usecases.application

/**
 * Sends the email carrying the link that completes a registration.
 */
interface SendRegistrationEmailUseCase {

    /**
     * @param email The address to send to.
     * @param code The registration code the link carries.
     * @param language The language to write in.
     */
    suspend operator fun invoke(email: String, code: String, language: String)

}
