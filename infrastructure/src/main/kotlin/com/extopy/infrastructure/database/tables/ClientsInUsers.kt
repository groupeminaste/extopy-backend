package com.extopy.infrastructure.database.tables

import com.extopy.domain.models.users.ClientInUser
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.timestamp

object ClientsInUsers : Table() {

    val code = varchar("code", 32)
    val userId = uuid("user_id")
    val clientId = uuid("client_id")

    // Note: the column is named `expiration`, which is what the deployed schema calls it.
    val expiresAt = timestamp("expiration")

    override val primaryKey = PrimaryKey(code)

    fun toClientInUser(
        row: ResultRow,
    ) = ClientInUser(
        row[code],
        row[userId],
        row[clientId],
        row[expiresAt]
    )

}
