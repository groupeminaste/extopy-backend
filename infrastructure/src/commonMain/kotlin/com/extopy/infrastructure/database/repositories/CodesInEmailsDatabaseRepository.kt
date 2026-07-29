package com.extopy.infrastructure.database.repositories

import com.extopy.domain.models.application.CodeInEmail
import com.extopy.domain.repositories.CodesInEmailsRepository
import com.extopy.infrastructure.database.TransactionManager
import com.extopy.infrastructure.database.tables.CodesInEmails
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import kotlin.time.Instant

class CodesInEmailsDatabaseRepository(
    private val transactionManager: TransactionManager,
) : CodesInEmailsRepository {

    init {
        transactionManager.transaction {
            SchemaUtils.create(CodesInEmails)
        }
    }

    override suspend fun get(code: String): CodeInEmail? =
        transactionManager.suspendTransaction {
            CodesInEmails
                .selectAll()
                .where { CodesInEmails.code eq code }
                .map(CodesInEmails::toCodeInEmail)
                .firstOrNull()
        }

    override suspend fun set(email: String, code: String, expiresAt: Instant): CodeInEmail? =
        transactionManager.suspendTransaction {
            // The address is the primary key, so asking for a second code replaces the first. The
            // update comes first because an insert on an address that already has one would fail on
            // the key, and a failed statement is not something to hand back to an open transaction.
            val updated = CodesInEmails.update({ CodesInEmails.email eq email }) {
                it[CodesInEmails.code] = code
                it[CodesInEmails.expiresAt] = expiresAt
            }
            if (updated == 0) CodesInEmails.insert {
                it[CodesInEmails.email] = email
                it[CodesInEmails.code] = code
                it[CodesInEmails.expiresAt] = expiresAt
            }
            CodeInEmail(email, code, expiresAt)
        }

    override suspend fun delete(code: String) {
        transactionManager.suspendTransaction {
            CodesInEmails.deleteWhere { CodesInEmails.code eq code }
        }
    }

}
