package me.nathanfallet.extopy.database.application

import me.nathanfallet.extopy.extensions.generateId
import me.nathanfallet.extopy.models.application.Client
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select

object Clients : Table() {

    val id = varchar("id", 32)
    val ownerId = varchar("owner_id", 32)
    val name = varchar("name", 255)
    val secret = varchar("secret", 255)
    val redirectUri = text("redirect_uri")

    override val primaryKey = PrimaryKey(id)

    fun generateId(): String {
        val candidate = String.generateId()
        return if (select { id eq candidate }.count() > 0) generateId() else candidate
    }

    fun toClient(
        row: ResultRow,
    ) = Client(
        row[id],
        row[ownerId],
        row[name],
        row[secret],
        row[redirectUri]
    )

}
