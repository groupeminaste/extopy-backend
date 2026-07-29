package com.extopy.infrastructure.database.tables

import com.extopy.domain.models.application.CodeInEmail
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.timestamp

object CodesInEmails : Table() {

    val email = varchar("email", 255)
    val code = varchar("code", 32).index()
    val expiresAt = timestamp("expires_at")

    // One code per address at a time: asking for a second one replaces the first.
    override val primaryKey = PrimaryKey(email)

    fun toCodeInEmail(
        row: ResultRow,
    ) = CodeInEmail(
        row[email],
        row[code],
        row[expiresAt]
    )

}
