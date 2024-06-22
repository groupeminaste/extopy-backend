package com.extopy.database.notifications

import com.extopy.models.notifications.TokenInNotification
import com.extopy.models.users.User
import dev.kaccelero.models.UUID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object TokensInNotifications : Table() {

    val token = varchar("token", 255)
    val service = varchar("service", 255)
    val clientId = uuid("client_id")
    val userId = uuid("user_id")
    val expiresAt = timestamp("expiresAt")

    override val primaryKey = PrimaryKey(arrayOf(token, service, clientId, userId))

    fun toTokenInNotification(
        row: ResultRow,
        user: User? = null,
    ) = TokenInNotification(
        row[token],
        row[service],
        UUID(row[clientId]),
        UUID(row[userId]),
        row[expiresAt],
        user
    )

}
