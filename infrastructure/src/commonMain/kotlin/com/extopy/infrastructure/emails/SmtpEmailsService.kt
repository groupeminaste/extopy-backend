package com.extopy.infrastructure.emails

import com.extopy.domain.models.application.Email
import com.extopy.domain.services.EmailsService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.commons.mail.HtmlEmail

/**
 * Implementation of [EmailsService] sending over SMTP.
 */
class SmtpEmailsService(
    private val host: String,
    private val username: String,
    private val password: String,
    private val port: Int = DEFAULT_PORT,
) : EmailsService {

    // Sending blocks on the socket, so it runs where blocking is allowed rather than on whichever
    // thread was handling the request.
    override suspend fun send(email: Email, destination: List<String>) = withContext(Dispatchers.IO) {
        destination.forEach { target ->
            HtmlEmail().apply {
                hostName = host
                isStartTLSEnabled = true
                setSmtpPort(port)
                setAuthentication(username, password)
                setFrom(username)
                addTo(target)
                subject = email.title
                setHtmlMsg(email.body)
            }.send()
        }
    }

    companion object {

        private const val DEFAULT_PORT = 587

    }

}
