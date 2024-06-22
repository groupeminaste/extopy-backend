package com.extopy.database.application

import com.extopy.models.application.CodeInEmail
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object CodesInEmails : Table() {

    val email = varchar("email", 255)
    val code = varchar("code", 32).index()
    val expiresAt = timestamp("expires_at")

    override val primaryKey = PrimaryKey(email)

    fun toCodeInEmail(
        row: ResultRow,
    ) = CodeInEmail(
        row[email],
        row[code],
        row[expiresAt]
    )

}
