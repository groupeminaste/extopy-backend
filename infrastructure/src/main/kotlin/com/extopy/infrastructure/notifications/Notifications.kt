package com.extopy.infrastructure.notifications

import com.extopy.domain.models.notifications.Notification
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.dao.id.UuidTable
import org.jetbrains.exposed.v1.datetime.timestamp

/**
 * Note: no repository creates or reads this table yet — the push notifications were started and not
 * finished. It is kept so that the shape does not have to be worked out again.
 *
 * It lives outside `database/tables` on purpose: the migrations are generated from that package, and
 * a table nobody uses has no business in the schema. Move it back when the feature is built, and
 * generate its migration then.
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
