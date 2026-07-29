package com.extopy.infrastructure.database.tables

import com.extopy.domain.models.notifications.TokenInNotification
import com.extopy.domain.models.users.User
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.timestamp

/**
 * Note: no repository creates or reads this table yet, see [Notifications].
 */
object TokensInNotifications : Table() {

    val token = varchar("token", 255)
    val service = varchar("service", 255)
    val clientId = uuid("client_id")
    val userId = uuid("user_id")

    // Note: the column is named `expiresAt`, which is what the deployed schema calls it.
    val expiresAt = timestamp("expiresAt")

    // A device is reachable once per client and per account.
    override val primaryKey = PrimaryKey(arrayOf(token, service, clientId, userId))

    fun toTokenInNotification(
        row: ResultRow,
        user: User? = null,
    ) = TokenInNotification(
        row[token],
        row[service],
        row[clientId],
        row[userId],
        row[expiresAt],
        user
    )

}
