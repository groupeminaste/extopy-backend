package me.nathanfallet.extopy.controllers.posts

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
import me.nathanfallet.extopy.models.application.ExtopyJson
import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.plugins.configureSerialization
import me.nathanfallet.usecases.models.UnitModel
import kotlin.test.Test
import kotlin.test.assertEquals

class PostsRouterTest {

    private val post = Post("id", "userId", body = "body")
    private val reply = Post("replyId", "userId", body = "body")

    private fun installApp(application: ApplicationTestBuilder): HttpClient {
        application.environment {
            config = ApplicationConfig("application.test.conf")
        }
        application.application {
            configureSerialization()
        }
        return application.createClient {
            install(ContentNegotiation) {
                json(ExtopyJson.json)
            }
        }
    }

    @Test
    fun testGetAPIv1() = testApplication {
        val client = installApp(this)
        val controller = mockk<IPostsController>()
        val router = PostsRouter(controller)
        coEvery { controller.get(any(), UnitModel, "id") } returns post
        routing {
            router.createRoutes(this)
        }
        assertEquals(post, client.get("/api/v1/posts/id").body())
    }

    @Test
    fun testGetReplies() = testApplication {
        val client = installApp(this)
        val controller = mockk<IPostsController>()
        val router = PostsRouter(controller)
        coEvery { controller.get(any(), UnitModel, "id") } returns post
        coEvery { controller.getReplies(any(), "id") } returns listOf(reply)
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/api/v1/posts/id/replies")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(listOf(reply), response.body())
    }

    @Test
    fun testGetRepliesOpenAPI() = testApplication {
        val client = installApp(this)
        val controller = mockk<IPostsController>()
        val router = PostsRouter(controller)
        val openAPI = OpenAPI()
        coEvery { controller.get(any(), UnitModel, "id") } returns post
        coEvery { controller.getReplies(any(), "id") } returns listOf(reply)
        routing {
            router.createRoutes(this, openAPI)
        }
        client.get("/api/v1/posts/id/replies")
        val get = openAPI.paths["/api/v1/posts/{postId}/replies"]?.get
        assertEquals("getPostRepliesById", get?.operationId)
        assertEquals(listOf("Post"), get?.tags)
        assertEquals("Get post replies by id", get?.description)
        assertEquals(1, get?.parameters?.size)
        assertEquals("postId", get?.parameters?.firstOrNull()?.name)
        assertEquals(1, get?.responses?.size)
        assertEquals(
            "#/components/schemas/${Post::class.qualifiedName}",
            get?.responses?.get("200")?.content?.get("application/json")?.schema?.items?.`$ref`
        )
    }

}
