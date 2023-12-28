package me.nathanfallet.extopy.database.application

import kotlinx.datetime.toInstant
import me.nathanfallet.extopy.models.application.CodeInEmail
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object CodesInEmails : Table() {

    val email = varchar("email", 255)
    val code = varchar("code", 32).index()
    val expiresAt = varchar("expires_at", 255)

    override val primaryKey = PrimaryKey(email)

    fun toCodeInEmail(
        row: ResultRow,
    ) = CodeInEmail(
        row[email],
        row[code],
        row[expiresAt].toInstant()
    )

}
