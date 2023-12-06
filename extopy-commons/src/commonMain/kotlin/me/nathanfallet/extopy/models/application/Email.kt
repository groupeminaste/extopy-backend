package me.nathanfallet.extopy.models.application

import me.nathanfallet.usecases.emails.IEmail

data class Email(
    val title: String,
    val body: String,
) : IEmail
