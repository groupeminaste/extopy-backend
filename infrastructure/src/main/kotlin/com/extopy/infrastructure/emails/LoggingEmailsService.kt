package com.extopy.infrastructure.emails

import com.extopy.domain.models.application.Email
import com.extopy.domain.services.EmailsService
import org.slf4j.LoggerFactory

/**
 * Implementation of [EmailsService] writing to the log instead of sending anything, which is what a
 * deployment without an SMTP host gets: a local run, and the tests.
 */
class LoggingEmailsService : EmailsService {

    private val logger = LoggerFactory.getLogger(LoggingEmailsService::class.java)

    override suspend fun send(email: Email, destination: List<String>) {
        logger.info("Not sending \"{}\" to {}: no SMTP host is configured", email.title, destination)
    }

}
