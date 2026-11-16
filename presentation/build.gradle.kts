plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.serialization)
    alias(libs.plugins.kover)
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        optIn.add("kotlin.uuid.ExperimentalUuidApi")
    }
}

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

    testImplementation(libs.kotlin.test.junit5)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(libs.tests.mockk)
    testImplementation(libs.tests.jsoup)
    testImplementation(libs.bundles.ktor.server.tests)
}

tasks.test {
    useJUnitPlatform()
}

// CI runs `jvmTest` across the build; on a JVM module that is the `test` task.
tasks.register("jvmTest") {
    dependsOn("test")
}
