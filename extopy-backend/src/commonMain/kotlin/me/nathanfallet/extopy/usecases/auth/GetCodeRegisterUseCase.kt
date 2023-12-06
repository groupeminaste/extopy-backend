package me.nathanfallet.extopy.usecases.auth

import io.ktor.server.application.*
import me.nathanfallet.extopy.models.auth.RegisterPayload
import me.nathanfallet.ktorx.usecases.auth.IGetCodeRegisterUseCase

class GetCodeRegisterUseCase : IGetCodeRegisterUseCase<RegisterPayload> {

    override suspend fun invoke(input1: ApplicationCall, input2: String): RegisterPayload? {
        TODO("Not yet implemented")
    }

}
