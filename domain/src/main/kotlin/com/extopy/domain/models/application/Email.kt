package com.extopy.domain.models.application

/**
 * An email to send, with its body already translated and rendered.
 */
data class Email(
    val title: String,
    val body: String,
)
