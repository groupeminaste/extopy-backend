package com.extopy.usecases.auth

import com.extopy.models.auth.ClientForUser
import com.extopy.repositories.users.IClientsInUsersRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus

class CreateAuthCodeUseCase(
    private val repository: IClientsInUsersRepository,
) : ICreateAuthCodeUseCase {

    override suspend fun invoke(input: ClientForUser): String? = repository.create(
        input.user.id,
        input.client.id,
        Clock.System.now().plus(1, DateTimeUnit.HOUR, TimeZone.currentSystemDefault())
    )?.code

}
