package com.extopy.models.application

import dev.kaccelero.commons.auth.IClient
import dev.kaccelero.models.IModel
import kotlinx.serialization.Serializable

@Serializable
data class Client(
    override val id: String,
    val ownerId: String,
    val name: String,
    val description: String,
    val secret: String,
    override val redirectUri: String,
) : IClient, IModel<String, Unit, Unit> {

    override val clientId = id
    override val clientSecret = secret

}
