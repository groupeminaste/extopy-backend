package com.extopy.database.application

import com.extopy.models.application.CodeInEmail
import kotlinx.datetime.Instant

interface ICodesInEmailsRepository {

    suspend fun getCodeInEmail(code: String): CodeInEmail?
    suspend fun getCodesInEmailsExpiringBefore(date: Instant): List<CodeInEmail>
    suspend fun createCodeInEmail(email: String, code: String, expiresAt: Instant): CodeInEmail?
    suspend fun updateCodeInEmail(email: String, code: String, expiresAt: Instant): Boolean
    suspend fun deleteCodeInEmail(code: String)

}
