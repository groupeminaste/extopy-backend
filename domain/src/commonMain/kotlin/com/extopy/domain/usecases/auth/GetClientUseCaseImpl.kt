package com.extopy.domain.usecases.auth

import com.extopy.domain.models.application.Client
import com.extopy.domain.repositories.ClientsRepository
import kotlin.uuid.Uuid

class GetClientUseCaseImpl(
    private val repository: ClientsRepository,
) : GetClientUseCase {

    override suspend fun invoke(id: Uuid): Client? = repository.get(id)

}
