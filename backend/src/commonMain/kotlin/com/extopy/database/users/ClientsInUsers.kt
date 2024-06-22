package com.extopy.database.users

import com.extopy.extensions.generateId
import com.extopy.models.users.ClientInUser
import dev.kaccelero.models.UUID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import org.jetbrains.exposed.sql.selectAll

object ClientsInUsers : Table() {

    val code = varchar("code", 32)
    val userId = uuid("user_id")
    val clientId = uuid("client_id")
    val expiration = timestamp("expiration")

    override val primaryKey = PrimaryKey(code)

    fun generateCode(): String {
        val candidate = String.generateId()
        return if (selectAll().where { code eq candidate }.count() > 0) generateCode() else candidate
    }

    fun toClientInUser(
        row: ResultRow,
    ) = ClientInUser(
        row[code],
        UUID(row[userId]),
        UUID(row[clientId]),
        row[expiration]
    )

}
