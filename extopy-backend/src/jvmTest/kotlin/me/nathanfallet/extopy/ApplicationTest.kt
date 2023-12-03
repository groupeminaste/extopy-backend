package me.nathanfallet.extopy

import io.ktor.server.config.*
import io.ktor.server.testing.*
import kotlin.test.Test

class ApplicationTest {

    @Test
    fun testStartup() = testApplication {
        environment {
            config = ApplicationConfig("application.test.conf")
        }
        application {
            module()
        }
        //val response = client.get("/api/v1/...")
        //assertEquals(HttpStatusCode.OK, response.status)
    }

}
