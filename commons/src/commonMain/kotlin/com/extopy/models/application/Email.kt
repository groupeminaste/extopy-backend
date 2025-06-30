package com.extopy.models.application

import dev.kaccelero.commons.emails.IEmail
import digital.guimauve.zodable.Zodable
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@Zodable
@JsExport
@Serializable
data class Email(
    val title: String,
    val body: String,
) : IEmail
