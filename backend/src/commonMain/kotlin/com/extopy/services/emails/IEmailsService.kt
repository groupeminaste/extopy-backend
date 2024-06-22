package com.extopy.services.emails

import dev.kaccelero.commons.emails.IEmail

interface IEmailsService {

    fun sendEmail(email: IEmail, destination: List<String>)

}
