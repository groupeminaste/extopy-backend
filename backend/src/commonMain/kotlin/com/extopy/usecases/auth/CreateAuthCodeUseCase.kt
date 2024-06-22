package com.extopy.usecases.auth

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import com.extopy.models.auth.ClientForUser
import com.extopy.models.users.User
import com.extopy.repositories.users.IClientsInUsersRepository

class CreateAuthCodeUseCase(
    private val repository: IClientsInUsersRepository,
) : ICreateAuthCodeUseCase {

    override suspend fun invoke(input: ClientForUser): String? = repository.create(
        (input.user as User).id,
        input.client.clientId,
        Clock.System.now().plus(1, DateTimeUnit.HOUR, TimeZone.currentSystemDefault())
    )?.code

}
