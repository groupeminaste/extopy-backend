package com.extopy.models.application

import dev.kaccelero.commons.auth.IClient
import dev.kaccelero.models.IModel
import dev.kaccelero.models.UUID
import digital.guimauve.zodable.Zodable
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@Zodable
@JsExport
@Serializable
data class Client(
    override val id: UUID,
    val ownerId: UUID,
    val name: String,
    val description: String,
    val secret: String,
    override val redirectUri: String,
) : IClient, IModel<UUID, Unit, Unit> {

    override val clientId: String
        get() = id.toString()

    override val clientSecret: String
        get() = secret

}
