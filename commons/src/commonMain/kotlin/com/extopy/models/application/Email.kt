package com.extopy.models.application

import dev.kaccelero.commons.emails.IEmail

data class Email(
    val title: String,
    val body: String,
) : IEmail
