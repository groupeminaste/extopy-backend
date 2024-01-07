package me.nathanfallet.extopy.database.users

import kotlinx.datetime.toInstant
import me.nathanfallet.extopy.extensions.generateId
import me.nathanfallet.extopy.models.users.ClientInUser
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.selectAll

object ClientsInUsers : Table() {

    val code = varchar("code", 32)
    val userId = varchar("user_id", 32)
    val clientId = varchar("client_id", 32)
    val expiration = varchar("expiration", 255)

    override val primaryKey = PrimaryKey(code)

    fun generateCode(): String {
        val candidate = String.generateId()
        return if (selectAll().where { code eq candidate }.count() > 0) generateCode() else candidate
    }

    fun toClientInUser(
        row: ResultRow,
    ) = ClientInUser(
        row[code],
        row[userId],
        row[clientId],
        row[expiration].toInstant()
    )

}
