package com.extopy.database.notifications

import com.extopy.models.notifications.Notification
import dev.kaccelero.models.UUID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object Notifications : UUIDTable() {

    val userId = uuid("user_id").index()
    val type = varchar("type", 255)
    val body = varchar("body", 255)
    val contentId = uuid("content_id").nullable()
    val publishedAt = timestamp("published_at")
    val expiresAt = timestamp("expires_at")
    val read = bool("read").default(false)

    fun toNotification(
        row: ResultRow,
    ) = Notification(
        UUID(row[id].value),
        row.getOrNull(userId)?.let(::UUID),
        row.getOrNull(type),
        row.getOrNull(body),
        row.getOrNull(contentId)?.let(::UUID),
        row.getOrNull(publishedAt),
        row.getOrNull(expiresAt),
        row.getOrNull(read)
    )

}
