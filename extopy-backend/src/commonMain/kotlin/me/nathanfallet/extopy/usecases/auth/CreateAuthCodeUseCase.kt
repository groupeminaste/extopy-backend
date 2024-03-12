package me.nathanfallet.extopy.usecases.auth

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import me.nathanfallet.extopy.models.auth.ClientForUser
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.extopy.repositories.users.IClientsInUsersRepository

class CreateAuthCodeUseCase(
    private val repository: IClientsInUsersRepository,
) : ICreateAuthCodeUseCase {

    override suspend fun invoke(input: ClientForUser): String? = repository.create(
        (input.user as User).id,
        input.client.clientId,
        Clock.System.now().plus(1, DateTimeUnit.HOUR, TimeZone.currentSystemDefault())
    )?.code

}
