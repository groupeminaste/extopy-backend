package com.extopy.domain.usecases.application

import com.extopy.domain.exceptions.auth.EmailAlreadyTakenException
import com.extopy.domain.models.application.CodeInEmail
import com.extopy.domain.repositories.CodesInEmailsRepository
import com.extopy.domain.repositories.UsersRepository
import com.extopy.domain.services.CodeGeneratorService
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours

class CreateCodeInEmailUseCaseImpl(
    private val repository: CodesInEmailsRepository,
    private val usersRepository: UsersRepository,
    private val codeGeneratorService: CodeGeneratorService,
) : CreateCodeInEmailUseCase {

    override suspend fun invoke(email: String): CodeInEmail? {
        if (usersRepository.getForUsernameOrEmail(email, includePassword = false) != null) {
            throw EmailAlreadyTakenException()
        }
        return repository.set(email, codeGeneratorService.generate(), Clock.System.now() + CODE_LIFETIME)
    }

    companion object {

        /**
         * How long someone has to follow the link before it has to be asked for again.
         */
        private val CODE_LIFETIME = 1.hours

    }

}
