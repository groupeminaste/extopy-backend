package me.nathanfallet.extopy.services.emails

import me.nathanfallet.usecases.emails.IEmail

interface IEmailsService {

    fun sendEmail(email: IEmail, destination: List<String>)

}
