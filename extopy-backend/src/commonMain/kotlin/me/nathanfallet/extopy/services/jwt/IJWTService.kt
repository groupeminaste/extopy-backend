package me.nathanfallet.extopy.services.jwt

interface IJWTService {

    fun generateJWT(userId: String, clientId: String, type: String): String

}
