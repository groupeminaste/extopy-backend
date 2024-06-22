package com.extopy.database.notifications

import kotlinx.datetime.toInstant
import kotlinx.serialization.Serializable
import com.extopy.models.notifications.TokenInNotification
import com.extopy.models.users.User
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object TokensInNotifications : Table() {

    val token = varchar("token", 255)
    val service = varchar("service", 255)
    val clientId = varchar("client_id", 32)
    val userId = varchar("user_id", 32)
    val expiration = varchar("expiration", 255)

    override val primaryKey = PrimaryKey(arrayOf(token, service, clientId, userId))

    fun toTokenInNotification(
        row: ResultRow,
        user: User? = null,
    ) = TokenInNotification(
        row[token],
        row[service],
        row[clientId],
        row[userId],
        row[expiration].toInstant(),
        user
    )

}

@Serializable
data class NotificationTokenUpload(val token: String, val service: String)
