package com.extopy.controllers.users

import com.extopy.models.posts.Post
import com.extopy.models.users.User
import com.extopy.plugins.configureSerialization
import dev.kaccelero.routers.createRoutes
import dev.kaccelero.serializers.Serialization
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.mockk
import io.swagger.v3.oas.models.OpenAPI
import kotlin.test.Test
import kotlin.test.assertEquals

class UsersRouterTest {

    private val user = User("id", "displayName", "username")
    private val post = Post("postId", "id", user, body = "body")

    private fun installApp(application: ApplicationTestBuilder): HttpClient {
        application.environment {
            config = ApplicationConfig("application.test.conf")
        }
        application.application {
            configureSerialization()
        }
        return application.createClient {
            install(ContentNegotiation) {
                json(Serialization.json)
            }
        }
    }

    @Test
    fun testGetAPIv1() = testApplication {
        val client = installApp(this)
        val controller = mockk<IUsersController>()
        val router = UsersRouter(controller)
        coEvery { controller.get(any(), "id") } returns user
        routing {
            router.createRoutes(this)
        }
        assertEquals(user, client.get("/api/v1/users/id").body())
    }

    @Test
    fun testGetPosts() = testApplication {
        val client = installApp(this)
        val controller = mockk<IUsersController>()
        val router = UsersRouter(controller)
        coEvery { controller.get(any(), "id") } returns user
        coEvery { controller.listPosts(any(), "id", null, null) } returns listOf(post)
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/api/v1/users/id/posts")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(listOf(post), response.body())
    }

    @Test
    fun testGetPostsOpenAPI() = testApplication {
        val client = installApp(this)
        val controller = mockk<IUsersController>()
        val router = UsersRouter(controller)
        val openAPI = OpenAPI()
        coEvery { controller.get(any(), "id") } returns user
        coEvery { controller.listPosts(any(), "id", null, null) } returns listOf(post)
        routing {
            router.createRoutes(this, openAPI)
        }
        client.get("/api/v1/users/id/posts")
        val get = openAPI.paths["/api/v1/users/{userId}/posts"]?.get
        assertEquals("listUserPost", get?.operationId)
        assertEquals(listOf("User"), get?.tags)
        assertEquals("Get user posts by id", get?.description)
        assertEquals(3, get?.parameters?.size)
        assertEquals("userId", get?.parameters?.firstOrNull()?.name)
        assertEquals(3, get?.responses?.size)
        assertEquals(
            "#/components/schemas/${Post::class.qualifiedName}",
            get?.responses?.get("200")?.content?.get("application/json")?.schema?.items?.`$ref`
        )
    }

}
