package com.extopy.infrastructure.database.tables

import com.extopy.domain.models.application.Client
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.dao.id.UuidTable

object Clients : UuidTable() {

    val ownerId = uuid("owner_id").index()
    val name = varchar("name", 255)
    val description = text("description")
    val secret = varchar("secret", 255)
    val redirectUri = text("redirect_uri")

    fun toClient(
        row: ResultRow,
    ) = Client(
        row[id].value,
        row[ownerId],
        row[name],
        row[description],
        row[secret],
        row[redirectUri]
    )

}
