package com.extopy.domain.usecases.auth

import com.extopy.domain.models.application.Client
import kotlin.uuid.Uuid

/**
 * Resolves an OAuth client from its ID.
 */
interface GetClientUseCase {

    suspend operator fun invoke(id: Uuid): Client?

}
