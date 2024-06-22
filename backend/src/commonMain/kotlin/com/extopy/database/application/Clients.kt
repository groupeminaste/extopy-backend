package com.extopy.database.application

import com.extopy.models.application.Client
import dev.kaccelero.models.UUID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ResultRow

object Clients : UUIDTable() {

    val ownerId = uuid("owner_id").index()
    val name = varchar("name", 255)
    val description = text("description")
    val secret = varchar("secret", 255)
    val redirectUri = text("redirect_uri")

    fun toClient(
        row: ResultRow,
    ) = Client(
        UUID(row[id].value),
        UUID(row[ownerId]),
        row[name],
        row[description],
        row[secret],
        row[redirectUri]
    )

}
