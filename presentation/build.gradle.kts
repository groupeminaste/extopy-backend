plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.serialization)
    alias(libs.plugins.kover)
}

kotlin {
    jvmToolchain(21)
    jvm {
        testRuns.named("test") {
            executionTask.configure {
                useJUnitPlatform()
            }
        }
    }

    applyDefaultHierarchyTemplate()
    sourceSets {
        all {
            languageSettings.apply {
                optIn("kotlin.uuid.ExperimentalUuidApi")
            }
        }
        val commonMain by getting {
            dependencies {
                api(projects.api)
                api(projects.domain)

                api(libs.koin.ktor)
                api(libs.ktor.server.core)
                api(libs.ktor.server.resources)
                api(libs.ktor.server.content.negotiation)
                api(libs.ktor.serialization.kotlinx.json)
                api(libs.ktor.server.status.pages)
                api(libs.ktor.server.freemarker)
                api(libs.ktor.server.cors)
                api(libs.ktor.server.openapi)
                // In Ktor 3.4.0, the OpenAPI plugin needs this one to read the routing tree.
                api(libs.ktor.server.routing.openapi)
                api(libs.ktor.server.auto.head.response)
                api(libs.ktor.server.call.logging)
                api(libs.ktor.server.auth.jwt)
                api(libs.ktor.server.sessions)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.tests.mockk)
                implementation(libs.tests.jsoup)
                implementation(libs.bundles.ktor.server.tests)
            }
        }
    }
}
