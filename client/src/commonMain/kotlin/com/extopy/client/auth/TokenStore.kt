package com.extopy.client.auth

/**
 * Where a client keeps its tokens between two runs.
 *
 * The client never decides where that is: a mobile application will want the keychain, a script an
 * environment variable, and the tests nothing at all.
 */
interface TokenStore {

    /**
     * Reads the tokens, or null if the client has not been authorized yet.
     */
    suspend fun load(): AuthTokens?

    /**
     * Stores the tokens, replacing whichever ones were there.
     */
    suspend fun save(tokens: AuthTokens)

    /**
     * Drops the tokens, which is what signing out means.
     */
    suspend fun clear()

}

/**
 * A [TokenStore] holding the tokens for as long as the process runs.
 */
class InMemoryTokenStore(
    private var tokens: AuthTokens? = null,
) : TokenStore {

    override suspend fun load(): AuthTokens? = tokens

    override suspend fun save(tokens: AuthTokens) {
        this.tokens = tokens
    }

    override suspend fun clear() {
        tokens = null
    }

}
