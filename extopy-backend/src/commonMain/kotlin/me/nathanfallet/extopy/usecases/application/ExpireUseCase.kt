package me.nathanfallet.extopy.usecases.application

import kotlinx.datetime.Instant

class ExpireUseCase : IExpireUseCase {

    override suspend fun invoke(input: Instant) {}

}
