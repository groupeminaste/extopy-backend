package com.extopy.presentation.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.extopy.domain.models.application.Client
import com.extopy.domain.models.posts.LikeInPost
import com.extopy.domain.models.posts.Post
import com.extopy.domain.models.timelines.Timeline
import com.extopy.domain.models.users.FollowerInUser
import com.extopy.domain.models.users.User
import com.extopy.domain.services.TranslateService
import com.extopy.domain.usecases.users.GetUserUseCase
import com.extopy.presentation.config.configureErrorHandling
import com.extopy.presentation.config.configureSerialization
import com.extopy.presentation.config.configureTemplating
import com.extopy.presentation.models.SessionPayload
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.resources.*
import io.ktor.server.sessions.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import kotlin.time.Instant
import kotlin.uuid.Uuid

/**
 * Everything the route tests need to stand up a Ktor application holding a single routes file, with
 * mocked use cases behind it.
 */
object RoutesTestHelper {

    val TEST_USER_ID: Uuid = Uuid.parse("00000000-0000-4000-8000-000000000001")
    val OTHER_USER_ID: Uuid = Uuid.parse("00000000-0000-4000-8000-000000000002")
    val TEST_POST_ID: Uuid = Uuid.parse("00000000-0000-4000-8000-000000000003")
    val TEST_CLIENT_ID: Uuid = Uuid.parse("00000000-0000-4000-8000-000000000004")

    const val API_JWT = "api-v1-jwt"

    private const val TEST_SECRET = "test-secret-key-for-jwt-testing-purposes"
    private const val TEST_ISSUER = "extopy"

    val testUser = User(
        id = TEST_USER_ID,
        displayName = "Nathan Fallet",
        username = "nathanfallet",
    )

    val otherUser = User(
        id = OTHER_USER_ID,
        displayName = "Someone Else",
        username = "someoneelse",
        personal = false,
    )

    val testPost = Post(
        id = TEST_POST_ID,
        userId = TEST_USER_ID,
        user = testUser,
        body = "Hello world!",
        publishedAt = Instant.parse("2026-01-02T03:04:05Z"),
    )

    val testLike = LikeInPost(
        postId = TEST_POST_ID,
        userId = TEST_USER_ID,
        user = testUser,
    )

    val testFollower = FollowerInUser(
        userId = TEST_USER_ID,
        targetId = OTHER_USER_ID,
        accepted = true,
    )

    val testTimeline = Timeline(Timeline.DEFAULT_ID)

    val testClient = Client(
        id = TEST_CLIENT_ID,
        ownerId = TEST_USER_ID,
        name = "Extopy for iOS",
        description = "The one on your phone",
        secret = "hunter2",
        redirectUri = "extopy://auth?code={code}",
    )

    /**
     * Signs a token the test application accepts, as the API clients would send.
     */
    fun generateTestToken(userId: Uuid = TEST_USER_ID): String = JWT.create()
        .withSubject(userId.toString())
        .withIssuer(TEST_ISSUER)
        .sign(Algorithm.HMAC256(TEST_SECRET))

    /**
     * Translates by echoing the key, which keeps the assertions on rendered pages readable.
     */
    private val translateService = object : TranslateService {
        override fun translate(key: String, language: String, args: List<String>): String =
            if (args.isEmpty()) key else "$key(${args.joinToString(",")})"
    }

    /**
     * Installs everything the routes rely on outside of their own handlers: the real serialization,
     * templating and error mapping, plus a token provider and cookie sessions holding no state.
     *
     * @param getUserUseCase The mock the `call.requireUser(...)` extension resolves users through.
     */
    fun Application.configureTestApplication(getUserUseCase: GetUserUseCase) {
        install(Koin) {
            modules(
                module {
                    single<TranslateService> { translateService }
                    single { getUserUseCase }
                }
            )
        }
        authentication {
            jwt(API_JWT) {
                verifier(JWT.require(Algorithm.HMAC256(TEST_SECRET)).withIssuer(TEST_ISSUER).build())
                validate { credential -> credential.payload.subject?.let { JWTPrincipal(credential.payload) } }
            }
        }
        install(Sessions) {
            cookie<SessionPayload>("session")
        }
        install(Resources)
        configureSerialization()
        configureTemplating()
        configureErrorHandling()
    }

}
