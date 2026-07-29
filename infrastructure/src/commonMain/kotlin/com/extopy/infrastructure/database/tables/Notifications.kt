package com.extopy.infrastructure.database.tables

import com.extopy.domain.models.notifications.Notification
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.dao.id.UuidTable
import org.jetbrains.exposed.v1.datetime.timestamp

/**
 * Note: no repository creates or reads this table yet — the push notifications were started and not
 * finished. It is kept so that the shape does not have to be worked out again.
 */
object Notifications : UuidTable() {

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
        row[id].value,
        row[userId],
        row[type],
        row[body],
        row.getOrNull(contentId),
        row.getOrNull(publishedAt),
        row.getOrNull(expiresAt),
        row.getOrNull(read)
    )

}
