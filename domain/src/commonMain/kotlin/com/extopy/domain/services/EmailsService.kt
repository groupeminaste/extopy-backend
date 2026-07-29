package com.extopy.domain.services

import com.extopy.domain.models.application.Email

/**
 * Sends the emails.
 */
interface EmailsService {

    /**
     * Sends an email to every address of [destination].
     */
    suspend fun send(email: Email, destination: List<String>)

}
