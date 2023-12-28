package me.nathanfallet.extopy.database.notifications

import kotlinx.datetime.toInstant
import me.nathanfallet.extopy.extensions.generateId
import me.nathanfallet.extopy.models.notifications.Notification
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select

object Notifications : Table() {

    val id = varchar("id", 32)
    val userId = varchar("user_id", 32).index()
    val type = varchar("type", 255)
    val body = varchar("body", 255)
    val contentId = varchar("contentId", 32).nullable()
    val published = varchar("published", 255)
    val expiration = varchar("expiration", 255)
    val read = bool("read").default(false)

    override val primaryKey = PrimaryKey(id)

    fun generateId(): String {
        val candidate = String.generateId()
        return if (select { id eq candidate }.count() > 0) generateId() else candidate
    }

    fun toNotification(
        row: ResultRow,
    ) = Notification(
        row[id],
        row.getOrNull(userId),
        row.getOrNull(type),
        row.getOrNull(body),
        row.getOrNull(contentId),
        row.getOrNull(published)?.toInstant(),
        row.getOrNull(expiration)?.toInstant(),
        row.getOrNull(read)
    )

}
