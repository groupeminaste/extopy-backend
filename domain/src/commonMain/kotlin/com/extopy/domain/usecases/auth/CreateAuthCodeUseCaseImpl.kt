package com.extopy.domain.usecases.auth

import com.extopy.domain.models.auth.ClientForUser
import com.extopy.domain.repositories.ClientsInUsersRepository
import com.extopy.domain.services.CodeGeneratorService
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours

class CreateAuthCodeUseCaseImpl(
    private val repository: ClientsInUsersRepository,
    private val codeGeneratorService: CodeGeneratorService,
) : CreateAuthCodeUseCase {

    override suspend fun invoke(clientForUser: ClientForUser): String? = repository.create(
        codeGeneratorService.generate(),
        clientForUser.user.id,
        clientForUser.client.id,
        Clock.System.now() + CODE_LIFETIME
    )?.code

    companion object {

        /**
         * How long a client has to exchange its code for a token.
         */
        private val CODE_LIFETIME = 1.hours

    }

}
