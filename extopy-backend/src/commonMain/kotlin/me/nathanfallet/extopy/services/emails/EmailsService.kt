package me.nathanfallet.extopy.services.emails

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import me.nathanfallet.extopy.models.application.Email
import me.nathanfallet.usecases.emails.IEmail
import org.apache.commons.mail.HtmlEmail

class EmailsService(
    private val host: String,
    private val username: String,
    private val password: String,
) : IEmailsService {

    override fun sendEmail(email: IEmail, destination: List<String>) {
        if (email !is Email) return
        CoroutineScope(Job()).launch {
            destination.forEach { target ->
                val htmlEmail = HtmlEmail()
                htmlEmail.hostName = host
                htmlEmail.isStartTLSEnabled = true
                htmlEmail.setSmtpPort(587)
                htmlEmail.setAuthentication(username, password)
                htmlEmail.setFrom(username)
                htmlEmail.addTo(target)
                htmlEmail.subject = email.title
                htmlEmail.setHtmlMsg(email.body)
                htmlEmail.send()
            }
        }
    }

}
