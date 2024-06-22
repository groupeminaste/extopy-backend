package com.extopy.repositories.application

import kotlinx.datetime.Instant
import com.extopy.models.application.CodeInEmail

interface ICodesInEmailsRepository {

    suspend fun getCodeInEmail(code: String): CodeInEmail?
    suspend fun getCodesInEmailsExpiringBefore(date: Instant): List<CodeInEmail>
    suspend fun createCodeInEmail(email: String, code: String, expiresAt: Instant): CodeInEmail?
    suspend fun updateCodeInEmail(email: String, code: String, expiresAt: Instant): Boolean
    suspend fun deleteCodeInEmail(code: String)

}
