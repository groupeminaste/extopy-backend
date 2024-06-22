package com.extopy.database.application

import kotlinx.datetime.Instant
import com.extopy.models.application.CodeInEmail
import com.extopy.repositories.application.ICodesInEmailsRepository
import dev.kaccelero.database.IDatabase
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class CodesInEmailsDatabaseRepository(
    private val database: IDatabase,
) : ICodesInEmailsRepository {

    init {
        database.transaction {
            SchemaUtils.create(CodesInEmails)
        }
    }

    override suspend fun getCodeInEmail(code: String): CodeInEmail? =
        database.suspendedTransaction {
            CodesInEmails
                .selectAll()
                .where { CodesInEmails.code eq code }
                .map(CodesInEmails::toCodeInEmail)
                .singleOrNull()
        }


    override suspend fun getCodesInEmailsExpiringBefore(date: Instant): List<CodeInEmail> =
        database.suspendedTransaction {
            CodesInEmails
                .selectAll()
                .where { CodesInEmails.expiresAt less date.toString() }
                .map(CodesInEmails::toCodeInEmail)
        }

    override suspend fun createCodeInEmail(
        email: String,
        code: String,
        expiresAt: Instant,
    ): CodeInEmail? =
        database.suspendedTransaction {
            CodesInEmails.insert {
                it[this.email] = email
                it[this.code] = code
                it[this.expiresAt] = expiresAt.toString()
            }.resultedValues?.map(CodesInEmails::toCodeInEmail)?.singleOrNull()
        }

    override suspend fun updateCodeInEmail(
        email: String,
        code: String,
        expiresAt: Instant,
    ): Boolean =
        database.suspendedTransaction {
            CodesInEmails.update({ CodesInEmails.email eq email }) {
                it[this.code] = code
                it[this.expiresAt] = expiresAt.toString()
            }
        } == 1

    override suspend fun deleteCodeInEmail(code: String) {
        database.suspendedTransaction {
            CodesInEmails.deleteWhere {
                CodesInEmails.code eq code
            }
        }
    }

}
